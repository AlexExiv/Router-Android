package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.speakerboxlite.processor.ksp.ext.nonSerializableProperties

internal class KspErrorReporter(private val logger: KSPLogger)
{
    var hadError: Boolean = false
        private set

    fun fail(message: String, symbol: KSNode? = null)
    {
        hadError = true
        if (symbol == null)
            logger.error(message)
        else
            logger.error(message, symbol)
    }

    fun error(annotation: KSAnnotation, name: String): Nothing?
    {
        fail("Router KSP: @${annotation.shortName.asString()} is missing required $name argument.", annotation)
        return null
    }

    fun failNonSerializablePath(path: KSClassDeclaration, symbol: KSNode)
    {
        fail(
            "Router KSP: ${path.qualifiedName?.asString()} is used as RoutePath, but all path properties must be Serializable. Non-serializable properties: ${path.nonSerializableProperties().joinToString(", ")}.",
            symbol)
    }
}
