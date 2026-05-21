package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toClassName

internal class MiddlewareProcessor(
    private val resolver: Resolver,
    private val logger: KSPLogger)
{
    private var middlewares = listOf<MiddlewareController>()
    private var middlewaresGlobal = listOf<MiddlewareController>()

    var hadComponent: Boolean = false
        private set

    fun prepareMiddlewares(): List<MiddlewareController>
    {
        val middlewareAnnotations = resolver.getSymbolsWithAnnotation(MIDDLEWARE_ANNOTATION)
            .mapNotNull {
                it as? KSClassDeclaration ?: run {
                    logger.error("Router KSP: @Middleware can be used only on annotation classes.", it)
                    null
                }
            }
            .toList()

        val middlewareControllers = mutableListOf<MiddlewareController>()
        middlewareAnnotations.forEach { annotation ->
            val fqName = annotation.qualifiedName?.asString()
            if (fqName == null)
            {
                logger.error("Router KSP: middleware annotation must have a qualified name.", annotation)
                return@forEach
            }

            val controllers = resolver.getSymbolsWithAnnotation(fqName)
                .mapNotNull { it as? KSClassDeclaration }
                .filter { it.hasAnyParent(MIDDLEWARE_CLASSES) }
                .toList()

            when (controllers.size)
            {
                0 -> {}
                1 ->
                {
                    val controller = controllers[0]
                    middlewareControllers.add(MiddlewareController(
                        annotation = annotation,
                        typeElement = controller,
                        className = controller.toClassName(),
                        varName = "mid_${controller.simpleName.asString().lowercase()}",
                        order = middlewareControllers.size,
                        hasComponent = controller.hasParent(MIDDLEWARE_COMPONENT_CLASS),
                        sourceFile = controller.containingFile))
                }
                else -> logger.error("Router KSP: middleware @${annotation.simpleName.asString()} must have exactly one MiddlewareController implementation. Found ${controllers.size}.", annotation)
            }
        }

        middlewares = middlewareControllers.toList()
        middlewaresGlobal = resolver.getSymbolsWithAnnotation(GLOBAL_MIDDLEWARE_ANNOTATION)
            .mapNotNull {
                it as? KSClassDeclaration ?: run {
                    logger.error("Router KSP: @GlobalMiddleware can be used only on classes.", it)
                    null
                }
            }
            .filter { it.hasAnyParent(MIDDLEWARE_CLASSES) }
            .map {
                MiddlewareController(
                    annotation = it,
                    typeElement = it,
                    className = it.toClassName(),
                    varName = "mid_${it.simpleName.asString().lowercase()}",
                    order = it.annotationValue<Int>(GLOBAL_MIDDLEWARE_ANNOTATION, "order") ?: 0,
                    hasComponent = it.hasParent(MIDDLEWARE_COMPONENT_CLASS),
                    sourceFile = it.containingFile)
            }
            .sortedBy { it.order }
            .toList()

        val total = middlewares + middlewaresGlobal
        hadComponent = total.any { it.hasComponent }
        return total
    }

    fun buildMiddlewares(typeElement: KSClassDeclaration): List<MiddlewareController>
    {
        val directMiddlewares = typeElement.collectAnnotations()
            .mapNotNull { annotation -> middlewares.firstOrNull { it.annotation == annotation } }

        return directMiddlewares.reversed() + middlewaresGlobal
    }

    companion object
    {
        const val MIDDLEWARE_COMPONENT_CLASS = "MiddlewareControllerComponent"
        val MIDDLEWARE_CLASSES = listOf("MiddlewareController", MIDDLEWARE_COMPONENT_CLASS)
    }
}
