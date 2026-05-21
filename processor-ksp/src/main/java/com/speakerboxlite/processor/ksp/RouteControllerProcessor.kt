package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator

internal class RouteControllerProcessor(
    codeGenerator: CodeGenerator,
    reporter: KspErrorReporter): RouteControllerProcessorBase(codeGenerator, reporter)
{
    override val controllerName = "RouteController"
    override val requiredTypeArgumentCount = 2
    override val pathIndex = 0
    override val viewIndex = 1
    override val vmIndex: Int? = null
    override val modelProviderIndex: Int? = null
    override val componentIndex: Int? = null
    override val requiredMethods = listOf(CREATE_VIEW)
}
