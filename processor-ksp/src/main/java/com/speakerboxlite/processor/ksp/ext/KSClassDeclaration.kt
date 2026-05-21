package com.speakerboxlite.processor.ksp.ext

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.speakerboxlite.processor.ksp.CHAIN_ANNOTATION
import com.speakerboxlite.processor.ksp.MAIN_ROUTER_PACK
import com.speakerboxlite.processor.ksp.ROUTE_ANNOTATION
import com.speakerboxlite.processor.ksp.RouteControllerProcessorBase
import com.speakerboxlite.processor.ksp.TABS_ANNOTATION
import com.speakerboxlite.processor.ksp.TabsProperties
import com.speakerboxlite.router.annotations.RouteType
import com.speakerboxlite.router.annotations.TabUnique
import com.squareup.kotlinpoet.ClassName

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

internal fun KSClassDeclaration.hasType(qualifiedName: String): Boolean =
    this.qualifiedName?.asString() == qualifiedName ||
        allSuperTypes().any { it.declaration.qualifiedName?.asString() == qualifiedName }

internal fun KSClassDeclaration.hasTypeSimple(name: String): Boolean = hasParent(name)

internal fun KSClassDeclaration.hasSerializableContract(): Boolean =
    qualifiedName?.asString() == "java.io.Serializable" || hasParent("Serializable")

internal fun KSClassDeclaration.routeType(): RouteType =
    when
    {
        hasType("$MAIN_ROUTER_PACK.ViewDialog") || hasParent("ViewDialog") -> RouteType.Dialog
        hasType("$MAIN_ROUTER_PACK.ViewBTS") || hasParent("ViewBTS") -> RouteType.BTS
        else -> RouteType.Simple
    }

internal fun KSClassDeclaration.allPropsAreSerializable(): Boolean =
    nonSerializableProperties().isEmpty()

internal fun KSClassDeclaration.nonSerializableProperties(): List<String> =
    serializableProperties()
        .flatMap { it.type.nonSerializablePropertyPaths(it.name, mutableSetOf()) }

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

internal fun KSClassDeclaration.annotation(fqName: String): KSAnnotation? =
    annotations.firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == fqName }

internal fun KSClassDeclaration.annotations(fqName: String): List<KSAnnotation> =
    annotations.filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == fqName }.toList()

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

internal fun KSClassDeclaration.isNothingOrVoid(): Boolean =
    qualifiedName?.asString() == "kotlin.Nothing" || qualifiedName?.asString() == "java.lang.Void"

internal fun KSClassDeclaration.nonSerializableNestedPropertyPaths(path: String, visited: MutableSet<String>): List<String>
{
    val name = qualifiedName?.asString() ?: return emptyList()

    if (!shouldInspectSerializableProperties() || !visited.add(name))
        return emptyList()

    return serializableProperties()
        .flatMap { it.type.nonSerializablePropertyPaths("$path.${it.name}", visited.toMutableSet()) }
}

private data class SerializableProperty(
    val name: String,
    val type: KSType)

private fun KSClassDeclaration.serializableProperties(): List<SerializableProperty>
{
    val properties = linkedMapOf<String, SerializableProperty>()

    primaryConstructor
        ?.parameters
        .orEmpty()
        .filter { it.isVal || it.isVar }
        .forEach {
            val name = it.name?.asString() ?: return@forEach
            properties[name] = SerializableProperty(name, it.type.resolve())
        }

    declarations
        .filterIsInstance<KSPropertyDeclaration>()
        .forEach {
            val name = it.simpleName.asString()
            properties.putIfAbsent(name, SerializableProperty(name, it.type.resolve()))
        }

    superTypes
        .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
        .filter { it.qualifiedName?.asString() != "$MAIN_ROUTER_PACK.RoutePath" }
        .flatMap { it.serializableProperties() }
        .forEach { properties.putIfAbsent(it.name, it) }

    return properties.values.toList()
}

private fun KSClassDeclaration.shouldInspectSerializableProperties(): Boolean
{
    val name = qualifiedName?.asString() ?: return false
    return containingFile != null &&
        !name.startsWith("kotlin.") &&
        !name.startsWith("java.") &&
        !name.startsWith("javax.") &&
        !name.startsWith("android.") &&
        !name.startsWith("androidx.")
}
