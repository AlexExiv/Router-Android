package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator

internal class RouteControllerCProcessor(
    codeGenerator: CodeGenerator,
    reporter: KspErrorReporter): RouteControllerProcessorBase(codeGenerator, reporter)
{
    override val controllerName = "RouteControllerC"
    override val requiredTypeArgumentCount = 3
    override val pathIndex = 0
    override val viewIndex = 1
    override val vmIndex: Int? = null
    override val modelProviderIndex: Int? = null
    override val componentIndex = 2
    override val requiredMethods = listOf(CREATE_VIEW, INJECT)
}
