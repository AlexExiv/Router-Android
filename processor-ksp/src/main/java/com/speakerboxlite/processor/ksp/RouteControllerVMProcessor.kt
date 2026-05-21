package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator

internal class RouteControllerVMProcessor(
    codeGenerator: CodeGenerator,
    reporter: KspErrorReporter): RouteControllerProcessorBase(codeGenerator, reporter)
{
    override val controllerName = "RouteControllerVM"
    override val requiredTypeArgumentCount = 4
    override val pathIndex = 0
    override val vmIndex = 1
    override val modelProviderIndex = 2
    override val viewIndex = 3
    override val componentIndex: Int? = null
    override val requiredMethods = listOf(CREATE_VIEW, CREATE_VIEWMODEL)
}
