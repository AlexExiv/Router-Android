package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger

internal class RouteControllerProcessor(
    codeGenerator: CodeGenerator,
    logger: KSPLogger): RouteControllerProcessorBase(codeGenerator, logger)
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
