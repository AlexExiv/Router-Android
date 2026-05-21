package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class RouterSymbolProcessorProvider: SymbolProcessorProvider
{
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        RouterSymbolProcessor(environment.codeGenerator, environment.logger)
}
