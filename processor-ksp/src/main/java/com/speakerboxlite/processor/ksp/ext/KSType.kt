package com.speakerboxlite.processor.ksp.ext

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

internal fun KSType.declarationClassName(): ClassName? =
    (declaration as? KSClassDeclaration)?.toClassName()

internal val KSType.isNullable: Boolean
    get() = nullability == Nullability.NULLABLE

internal fun KSType.isBundleSupported(): Boolean =
    isSerializableType() || isParcelable()

internal fun KSType.isParcelable(): Boolean =
    (declaration as? KSClassDeclaration)?.hasType("android.os.Parcelable") == true

internal fun KSType.nonSerializablePropertyPaths(path: String, visited: MutableSet<String>): List<String> =
    when (val decl = declaration)
    {
        is KSClassDeclaration ->
        {
            val name = decl.qualifiedName?.asString()
            when
            {
                name in serializableValueTypes || decl.classKind == ClassKind.ENUM_CLASS -> emptyList()
                !decl.hasType("java.io.Serializable") -> listOf("$path: ${name ?: decl.simpleName.asString()}")
                else ->
                {
                    val typeArgumentErrors = arguments
                        .mapNotNull { it.type?.resolve() }
                        .flatMapIndexed { index, type -> type.nonSerializablePropertyPaths("$path<$index>", visited.toMutableSet()) }

                    if (typeArgumentErrors.isNotEmpty())
                        typeArgumentErrors
                    else
                        decl.nonSerializableNestedPropertyPaths(path, visited)
                }
            }
        }
        is KSTypeAlias -> decl.type.resolve().nonSerializablePropertyPaths(path, visited)
        is KSTypeParameter ->
        {
            val bounds = decl.bounds.map { it.resolve() }.toList()
            if (bounds.isEmpty())
                listOf("$path: ${decl.name.asString()}")
            else if (bounds.any { it.isSerializableType() })
                emptyList()
            else
                listOf("$path: ${decl.name.asString()}")
        }
        else -> listOf("$path: ${declaration.simpleName.asString()}")
    }

private fun KSType.isSerializableType(): Boolean =
    nonSerializablePropertyPaths("", mutableSetOf()).isEmpty()

private val serializableValueTypes = setOf(
    "kotlin.String",
    "kotlin.Int",
    "kotlin.Long",
    "kotlin.Boolean",
    "kotlin.Float",
    "kotlin.Double",
    "kotlin.Short",
    "kotlin.Byte",
    "kotlin.Char",
    "kotlin.IntArray",
    "kotlin.LongArray",
    "kotlin.BooleanArray",
    "kotlin.FloatArray",
    "kotlin.DoubleArray",
    "kotlin.ShortArray",
    "kotlin.ByteArray",
    "kotlin.CharArray")
