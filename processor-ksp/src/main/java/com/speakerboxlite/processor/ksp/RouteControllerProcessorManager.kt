package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal class RouteControllerProcessorManager(
    codeGenerator: CodeGenerator,
    private val logger: KSPLogger)
{
    private val processors = mutableListOf<RouteControllerProcessorInterface>()

    var hadComponent: Boolean = false
        private set

    init
    {
        register(RouteControllerVMCProcessor(codeGenerator, logger))
        register(RouteControllerVMProcessor(codeGenerator, logger))
        register(RouteControllerCProcessor(codeGenerator, logger))
        register(RouteControllerProcessor(codeGenerator, logger))
    }

    fun createClass(element: KSClassDeclaration): RouteClass?
    {
        val processor = processors.firstOrNull { it.checkElement(element) }
        if (processor == null)
        {
            logger.error(
                "Router KSP: @Route class ${element.qualifiedName?.asString()} must extend RouteController, RouteControllerVM, RouteControllerC, or RouteControllerVMC.",
                element)
            return null
        }

        val routeClass = processor.createClass(element) ?: return null
        if ((processor as? RouteControllerProcessorBase)?.hasComponent == true)
            hadComponent = true
        return routeClass
    }

    fun register(processor: RouteControllerProcessorInterface)
    {
        processors.add(processor)
    }
}
