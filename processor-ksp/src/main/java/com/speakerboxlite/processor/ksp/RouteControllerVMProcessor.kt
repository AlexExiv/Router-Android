package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger

internal class RouteControllerVMProcessor(
    codeGenerator: CodeGenerator,
    logger: KSPLogger): RouteControllerProcessorBase(codeGenerator, logger)
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
