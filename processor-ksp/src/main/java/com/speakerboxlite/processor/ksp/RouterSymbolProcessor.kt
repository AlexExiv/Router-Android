package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.speakerboxlite.processor.ksp.ext.annotations

internal class RouterSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger): SymbolProcessor
{
    private var processed = false

    override fun process(resolver: Resolver): List<KSAnnotated>
    {
        if (processed)
            return emptyList()

        val reporter = KspErrorReporter(logger)
        val routeSymbols = resolver.getSymbolsWithAnnotation(ROUTE_ANNOTATION).toList()
        val fragmentRouteSymbols = resolver.getSymbolsWithAnnotation(ROUTER_FRAGMENT_ROUTE_ANNOTATION).toList()
        val fragmentFactorySymbols = resolver.getSymbolsWithAnnotation(ROUTER_FRAGMENT_ANNOTATION).toList()
        val allSymbols = routeSymbols + fragmentRouteSymbols + fragmentFactorySymbols
        val invalid = allSymbols.filterNot { it.validate() }.toList()
        if (invalid.isNotEmpty())
            return invalid

        val routeDeclarations = routeSymbols
            .mapNotNull {
                it as? KSClassDeclaration ?: run {
                    reporter.fail("Router KSP: @Route can be used only on classes.", it)
                    null
                }
            }
            .toList()

        val fragmentDeclarations = (fragmentRouteSymbols + fragmentFactorySymbols)
            .mapNotNull {
                it as? KSClassDeclaration ?: run {
                    reporter.fail("Router KSP: @RouterFragmentRoute and @RouterFragment can be used only on classes.", it)
                    null
                }
            }
            .distinctBy { it.qualifiedName?.asString() }
            .toList()

        if (routeDeclarations.isEmpty() && fragmentDeclarations.isEmpty())
            return emptyList()

        val routerApp = RouterAppProcessor(reporter).findRouterApp(resolver) ?: return emptyList()
        val middlewareProcessor = MiddlewareProcessor(resolver, reporter)
        val middlewares = middlewareProcessor.prepareMiddlewares()

        val routeProcessor = RouteControllerProcessorManager(codeGenerator, reporter)
        val routes = routeDeclarations.mapNotNull {
            routeProcessor.createClass(it)?.copy(middlewares = middlewareProcessor.buildMiddlewares(it))
        }
        val fragmentRouteProcessor = FragmentRouteProcessor(codeGenerator, reporter, routerApp)
        val fragmentRoutes = fragmentDeclarations.flatMap { f ->
            fragmentRouteProcessor.createClasses(f)
                .map { it.copy(middlewares = middlewareProcessor.buildMiddlewares(f)) }
        }

        if (routes.size != routeDeclarations.size)
            return emptyList()
        if (fragmentRouteProcessor.hadError)
            return emptyList()

        RouterComponentProcessor(codeGenerator)
            .generate(
                routerApp,
                routes + fragmentRoutes,
                middlewares,
                routeProcessor.hadComponent || fragmentRoutes.any { it.componentCntrl } || middlewareProcessor.hadComponent)

        processed = true
        return emptyList()
    }
}
