package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator

internal class RouteControllerVMCProcessor(
    codeGenerator: CodeGenerator,
    reporter: KspErrorReporter): RouteControllerProcessorBase(codeGenerator, reporter)
{
    override val controllerName = "RouteControllerVMC"
    override val requiredTypeArgumentCount = 5
    override val pathIndex = 0
    override val vmIndex = 1
    override val modelProviderIndex = 2
    override val viewIndex = 3
    override val componentIndex = 4
    override val requiredMethods = listOf(CREATE_VIEW, CREATE_VIEWMODEL, INJECT)
}
