package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.speakerboxlite.router.annotations.TabUnique
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

internal fun KSClassDeclaration.tabsProperties(): TabsProperties?
{
    val annotation = annotation(TABS_ANNOTATION) ?: return null
    return TabsProperties(
        tabRouteInParent = annotation.value("tabRouteInParent") ?: false,
        backToFirst = annotation.value("backToFirst") ?: true,
        tabUnique = enumValue(annotation.value("tabUnique"), TabUnique.Class))
}

internal fun KSClassDeclaration.collectAnnotations(): List<KSClassDeclaration>
{
    val current = annotations
        .mapNotNull { it.annotationType.resolve().declaration as? KSClassDeclaration }
        .toList()

    val inherited = superTypes
        .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
        .flatMap { it.collectAnnotations() }
        .toList()

    return current + inherited
}

internal fun KSClassDeclaration.hasAnyParent(names: List<String>): Boolean =
    names.any { hasParent(it) }

internal fun KSClassDeclaration.hasParent(name: String): Boolean
{
    if (simpleName.asString() == name)
        return true

    return allSuperTypes().any { it.declaration.simpleName.asString() == name }
}

internal fun KSClassDeclaration.getDeclaredFunctions(): Sequence<KSFunctionDeclaration> =
    declarations.filterIsInstance<KSFunctionDeclaration>()

internal fun KSClassDeclaration.allSuperTypes(): Sequence<KSType> = sequence {
    for (superTypeRef in superTypes)
    {
        val superType = superTypeRef.resolve()
        yield(superType)

        val declaration = superType.declaration
        when (declaration)
        {
            is KSClassDeclaration -> yieldAll(declaration.allSuperTypes())
            is KSTypeAlias ->
            {
                val expanded = declaration.type.resolve()
                yield(expanded)
                (expanded.declaration as? KSClassDeclaration)?.let { yieldAll(it.allSuperTypes()) }
            }
        }
    }
}

internal fun KSClassDeclaration.resolveControllerArguments(processor: RouteControllerProcessorBase): List<KSClassDeclaration>?
{
    for (superTypeRef in superTypes)
    {
        val superType = superTypeRef.resolve()
        val declaration = superType.declaration

        if (declaration.simpleName.asString() == processor.controllerName)
            return superType.arguments.mapClassDeclarations()

        if (declaration is KSTypeAlias)
        {
            val actualArguments = declaration.typeParameters
                .mapIndexedNotNull { index, param ->
                    val actual = superType.arguments.getOrNull(index)?.type?.resolve()?.declaration as? KSClassDeclaration
                    actual?.let { param.name.asString() to it }
                }
                .toMap()

            val expanded = declaration.type.resolve()
            if (expanded.declaration.simpleName.asString() == processor.controllerName)
                return expanded.arguments.mapClassDeclarations(actualArguments)
        }

        if (declaration is KSClassDeclaration)
        {
            val nested = declaration.resolveControllerArguments(processor)
            if (nested != null)
                return nested
        }
    }

    return null
}

internal fun List<KSTypeArgument>.mapClassDeclarations(typeAliasArguments: Map<String, KSClassDeclaration> = mapOf()): List<KSClassDeclaration>
{
    return mapNotNull { argument ->
        when (val declaration = argument.type?.resolve()?.declaration)
        {
            is KSClassDeclaration -> declaration
            is KSTypeParameter -> typeAliasArguments[declaration.name.asString()]
            else -> null
        }
    }
}

internal fun KSClassDeclaration.annotation(fqName: String): KSAnnotation? =
    annotations.firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == fqName }

internal inline fun <reified T> KSClassDeclaration.annotationValue(fqName: String, name: String): T? =
    annotation(fqName)?.value(name)

internal inline fun <reified E: Enum<E>> KSClassDeclaration.annotationEnum(fqName: String, name: String, default: E): E =
    enumValue(annotation(fqName)?.value(name), default)

internal fun KSClassDeclaration.annotationKClass(fqName: String, name: String): ClassName? =
    annotation(fqName)?.value<KSType>(name)?.declarationClassName()

internal fun KSClassDeclaration.annotationKClassList(fqName: String, name: String): List<ClassName> =
    annotation(fqName)
        ?.value<List<*>>(name)
        ?.mapNotNull { (it as? KSType)?.declarationClassName() }
        ?: listOf()

internal inline fun <reified T> KSAnnotation.value(name: String): T?
{
    val value = arguments.firstOrNull { it.name?.asString() == name }?.value ?: return null
    return value as? T
}

internal inline fun <reified E: Enum<E>> enumValue(value: Any?, default: E): E =
    value?.toString()?.substringAfterLast(".")?.let { runCatching { enumValueOf<E>(it) }.getOrNull() } ?: default

internal fun KSType.declarationClassName(): ClassName? =
    (declaration as? KSClassDeclaration)?.toClassName()
