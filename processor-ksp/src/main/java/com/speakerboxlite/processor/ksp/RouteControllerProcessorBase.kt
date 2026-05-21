package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.RouteType
import com.speakerboxlite.router.annotations.SingleTop
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

internal abstract class RouteControllerProcessorBase(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger): RouteControllerProcessorInterface
{
    abstract val controllerName: String
    abstract val requiredTypeArgumentCount: Int
    abstract val pathIndex: Int
    abstract val viewIndex: Int
    abstract val vmIndex: Int?
    abstract val modelProviderIndex: Int?
    abstract val componentIndex: Int?
    abstract val requiredMethods: List<String>

    val hasComponent: Boolean
        get() = componentIndex != null

    override fun checkElement(element: KSClassDeclaration): Boolean =
        element.hasParent(controllerName)

    override fun createClass(element: KSClassDeclaration): RouteClass?
    {
        val typeArguments = element.resolveControllerArguments(this)
        if (typeArguments == null)
        {
            logger.error("Router KSP: couldn't resolve $controllerName supertype for ${element.qualifiedName?.asString()}. Avoid unsupported generic typealias shapes.", element)
            return null
        }

        if (typeArguments.size < requiredTypeArgumentCount)
        {
            logger.error("Router KSP: ${element.qualifiedName?.asString()} has invalid $controllerName type arguments.", element)
            return null
        }

        fun classArg(index: Int, label: String): KSClassDeclaration? =
            typeArguments.getOrNull(index) ?: run {
                logger.error("Router KSP: couldn't resolve $label type argument for ${element.qualifiedName?.asString()}.", element)
                null
            }

        val pathDecl = classArg(pathIndex, "path") ?: return null
        val viewDecl = classArg(viewIndex, "view") ?: return null
        val vmDecl = vmIndex?.let { classArg(it, "view model") }
        val mpDecl = modelProviderIndex?.let { classArg(it, "model provider") }
        val componentDecl = componentIndex?.let { classArg(it, "component") }

        if ((vmIndex != null && vmDecl == null) ||
            (modelProviderIndex != null && mpDecl == null) ||
            (componentIndex != null && componentDecl == null))
            return null

        val declaredMethodNames = element.getDeclaredFunctions().map { it.simpleName.asString() }.toSet()
        val routeClassName = if (declaredMethodNames.containsAll(requiredMethods))
            element.toClassName()
        else
            generateImplementation(element, declaredMethodNames, pathDecl, viewDecl, vmDecl, mpDecl, componentDecl)

        return RouteClass(
            className = routeClassName,
            pathName = pathDecl.toClassName(),
            viewName = viewDecl.toClassName(),
            componentCntrl = hasComponent,
            componentName = componentDecl?.toClassName(),
            isCompose = viewDecl.hasParent("ViewCompose"),
            routeType = getRouteType(viewDecl),
            sourceFile = element.containingFile,
            uri = element.annotationValue(ROUTE_ANNOTATION, "uri") ?: "",
            presentation = element.annotationEnum(ROUTE_ANNOTATION, "presentation", Presentation.Push),
            singleTop = element.annotationEnum(ROUTE_ANNOTATION, "singleTop", SingleTop.None),
            tabsProperties = element.tabsProperties(),
            animationClass = element.annotationKClass(ROUTE_ANNOTATION, "animation")?.takeUnless { it.isEmptyAnimationMarker() },
            chainPaths = element.annotationKClassList(CHAIN_ANNOTATION, "closeItems"),
            creatingInjector = declaredMethodNames.contains(CREATE_INJECTOR),
            middlewares = emptyList())
    }

    protected fun getRouteType(view: KSClassDeclaration): RouteType =
        when
        {
            view.hasParent("ViewDialog") -> RouteType.Dialog
            view.hasParent("ViewBTS") -> RouteType.BTS
            else -> RouteType.Simple
        }

    private fun generateImplementation(
        element: KSClassDeclaration,
        declaredMethodNames: Set<String>,
        pathDecl: KSClassDeclaration,
        viewDecl: KSClassDeclaration,
        vmDecl: KSClassDeclaration?,
        mpDecl: KSClassDeclaration?,
        componentDecl: KSClassDeclaration?): ClassName
    {
        val classNameImpl = "${element.simpleName.asString()}_IMP"
        val classBuilder = TypeSpec.classBuilder(classNameImpl)

        if (!declaredMethodNames.contains(CREATE_VIEW))
        {
            classBuilder.addFunction(FunSpec.builder(CREATE_VIEW)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("path", pathDecl.toClassName())
                .returns(viewDecl.toClassName())
                .addStatement("return %T()", viewDecl.toClassName())
                .build())
        }

        if (vmIndex != null && !declaredMethodNames.contains(CREATE_VIEWMODEL))
        {
            classBuilder.addFunction(FunSpec.builder(CREATE_VIEWMODEL)
                .addModifiers(KModifier.OVERRIDE, KModifier.PROTECTED)
                .addParameter("modelProvider", mpDecl!!.toClassName())
                .addParameter("path", pathDecl.toClassName())
                .returns(vmDecl!!.toClassName())
                .addStatement("return modelProvider.getViewModel()")
                .build())
        }

        if (componentIndex != null && !declaredMethodNames.contains(INJECT))
        {
            val targetName = if (vmIndex == null) "view" else "vm"
            val targetType = if (vmIndex == null) viewDecl.toClassName() else vmDecl!!.toClassName()
            classBuilder.addFunction(FunSpec.builder(INJECT)
                .addModifiers(KModifier.OVERRIDE, KModifier.PROTECTED)
                .addParameter(targetName, targetType)
                .addParameter("component", componentDecl!!.toClassName())
                .addStatement("component.inject($targetName)")
                .build())
        }

        classBuilder.superclass(element.toClassName())

        FileSpec.builder(element.packageName.asString(), classNameImpl)
            .addType(classBuilder.build())
            .build()
            .writeTo(codeGenerator, Dependencies(aggregating = false, sources = listOfNotNull(element.containingFile).toTypedArray()))

        return ClassName(element.packageName.asString(), classNameImpl)
    }

    private fun ClassName.isEmptyAnimationMarker(): Boolean =
        simpleName == "Nothing" || canonicalName == "java.lang.Void"
}
