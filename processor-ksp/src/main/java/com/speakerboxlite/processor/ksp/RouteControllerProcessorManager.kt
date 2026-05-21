package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal class RouteControllerProcessorManager(
    codeGenerator: CodeGenerator,
    private val reporter: KspErrorReporter)
{
    private val processors = mutableListOf<RouteControllerProcessorInterface>()

    var hadComponent: Boolean = false
        private set

    init
    {
        register(RouteControllerVMCProcessor(codeGenerator, reporter))
        register(RouteControllerVMProcessor(codeGenerator, reporter))
        register(RouteControllerCProcessor(codeGenerator, reporter))
        register(RouteControllerProcessor(codeGenerator, reporter))
    }

    fun createClass(element: KSClassDeclaration): RouteClass?
    {
        val processor = processors.firstOrNull { it.checkElement(element) }
        if (processor == null)
        {
            reporter.fail(
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
