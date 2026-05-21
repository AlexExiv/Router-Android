package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

internal class RouterSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger): SymbolProcessor
{
    private var processed = false

    override fun process(resolver: Resolver): List<KSAnnotated>
    {
        if (processed)
            return emptyList()

        val routeSymbols = resolver.getSymbolsWithAnnotation(ROUTE_ANNOTATION)
        val invalid = routeSymbols.filterNot { it.validate() }.toList()
        if (invalid.isNotEmpty())
            return invalid

        val routeDeclarations = routeSymbols
            .mapNotNull {
                it as? KSClassDeclaration ?: run {
                    logger.error("Router KSP: @Route can be used only on classes.", it)
                    null
                }
            }
            .toList()

        if (routeDeclarations.isEmpty())
            return emptyList()

        val routerApp = RouterAppProcessor(logger).findRouterApp(resolver) ?: return emptyList()
        val middlewareProcessor = MiddlewareProcessor(resolver, logger)
        val middlewares = middlewareProcessor.prepareMiddlewares()

        val routeProcessor = RouteControllerProcessorManager(codeGenerator, logger)
        val routes = routeDeclarations.mapNotNull {
            routeProcessor.createClass(it)?.copy(middlewares = middlewareProcessor.buildMiddlewares(it))
        }

        if (routes.size != routeDeclarations.size)
            return emptyList()

        RouterComponentProcessor(codeGenerator)
            .generate(routerApp, routes, middlewares, routeProcessor.hadComponent || middlewareProcessor.hadComponent)

        processed = true
        return emptyList()
    }
}
