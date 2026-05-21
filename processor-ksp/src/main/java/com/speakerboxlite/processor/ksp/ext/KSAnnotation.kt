package com.speakerboxlite.processor.ksp.ext

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName

internal inline fun <reified T> KSAnnotation.value(name: String): T?
{
    val value = arguments.firstOrNull { it.name?.asString() == name }?.value ?: return null
    return value as? T
}

internal inline fun <reified E: Enum<E>> KSAnnotation.enumValue(name: String, default: E): E =
    enumValue(value(name), default)

internal fun KSAnnotation.kClass(name: String): KSClassDeclaration? =
    value<KSType>(name)?.declaration as? KSClassDeclaration

internal fun KSAnnotation.className(name: String): ClassName? =
    value<KSType>(name)?.declarationClassName()

internal inline fun <reified E: Enum<E>> enumValue(value: Any?, default: E): E =
    value?.toString()?.substringAfterLast(".")?.let { runCatching { enumValueOf<E>(it) }.getOrNull() } ?: default
