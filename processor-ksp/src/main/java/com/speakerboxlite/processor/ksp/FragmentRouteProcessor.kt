package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.speakerboxlite.processor.ksp.ext.allPropsAreSerializable
import com.speakerboxlite.processor.ksp.ext.annotationKClassList
import com.speakerboxlite.processor.ksp.ext.annotations
import com.speakerboxlite.processor.ksp.ext.className
import com.speakerboxlite.processor.ksp.ext.enumValue
import com.speakerboxlite.processor.ksp.ext.getDeclaredFunctions
import com.speakerboxlite.processor.ksp.ext.hasType
import com.speakerboxlite.processor.ksp.ext.isBundleSupported
import com.speakerboxlite.processor.ksp.ext.isEmptyAnimationMarker
import com.speakerboxlite.processor.ksp.ext.isNothingOrVoid
import com.speakerboxlite.processor.ksp.ext.isParcelable
import com.speakerboxlite.processor.ksp.ext.kClass
import com.speakerboxlite.processor.ksp.ext.routeType
import com.speakerboxlite.processor.ksp.ext.tabsProperties
import com.speakerboxlite.processor.ksp.ext.value
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.SingleTop
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

internal class FragmentRouteProcessor(
    private val codeGenerator: CodeGenerator,
    private val reporter: KspErrorReporter,
    private val routerApp: RouterAppInfo)
{
    val hadError: Boolean
        get() = reporter.hadError

    fun createClasses(fragment: KSClassDeclaration): List<RouteClass>
    {
        val fastRoutes = fragment.annotations(ROUTER_FRAGMENT_ROUTE_ANNOTATION)
            .mapNotNull { createClass(fragment, it, FragmentFactory.Bundle) }
        val factoryRoutes = fragment.annotations(ROUTER_FRAGMENT_ANNOTATION)
            .mapNotNull { createClass(fragment, it, FragmentFactory.Method) }
        return fastRoutes + factoryRoutes
    }

    private fun createClass(fragment: KSClassDeclaration, annotation: KSAnnotation, factory: FragmentFactory): RouteClass?
    {
        val pathDecl = annotation.kClass("path") ?: return reporter.error(annotation, "path")
        val vmDecl = annotation.kClass("viewModel")?.takeUnless { it.isNothingOrVoid() }
        val modelProviderDecl = annotation.kClass("modelProvider")?.takeUnless { it.isNothingOrVoid() }
        val componentDecl = annotation.kClass("component")?.takeUnless { it.isNothingOrVoid() }

        if (!pathDecl.hasType("$MAIN_ROUTER_PACK.RoutePath"))
        {
            reporter.fail(
                "Router KSP: ${fragment.qualifiedName?.asString()} uses ${pathDecl.qualifiedName?.asString()} as path, but it must implement $MAIN_ROUTER_PACK.RoutePath.",
                fragment)
            return null
        }

        if (!pathDecl.allPropsAreSerializable())
        {
            reporter.failNonSerializablePath(pathDecl, fragment)
            return null
        }

        if (vmDecl == null)
        {
            if (!fragment.hasType("$MAIN_ROUTER_PACK.fragment.ViewFragment"))
            {
                reporter.fail(
                    "Router KSP: ${fragment.qualifiedName?.asString()} is annotated with @${annotation.shortName.asString()}, but it must implement $MAIN_ROUTER_PACK.fragment.ViewFragment. Extend router.fragment.bootstrap.Fragment or implement ViewFragment manually.",
                    fragment)
                return null
            }
        }
        else
        {
            if (!fragment.hasType("$MAIN_ROUTER_PACK.fragment.ViewFragmentVM"))
            {
                reporter.fail(
                    "Router KSP: ${fragment.qualifiedName?.asString()} declares viewModel = ${vmDecl.qualifiedName?.asString()}::class, but it must implement $MAIN_ROUTER_PACK.fragment.ViewFragmentVM<${vmDecl.simpleName.asString()}>. Extend FragmentViewModel<${vmDecl.simpleName.asString()}> or implement ViewFragmentVM manually.",
                    fragment)
                return null
            }
        }

        if (!fragment.hasType("androidx.fragment.app.Fragment"))
        {
            reporter.fail(
                "Router KSP: ${fragment.qualifiedName?.asString()} is annotated with @${annotation.shortName.asString()}, but it must extend androidx.fragment.app.Fragment.",
                fragment)
            return null
        }

        val factoryName = when (factory)
        {
            FragmentFactory.Bundle -> null
            FragmentFactory.Method -> annotation.value<String>("factory").orEmpty().ifBlank { routerApp.viewFactoryName }
        }

        if (factory == FragmentFactory.Bundle && !validateBundlePath(pathDecl, fragment))
            return null

        if (factoryName != null && !validateFactory(fragment, pathDecl, factoryName))
            return null

        val className = generateController(fragment, pathDecl, vmDecl, modelProviderDecl, componentDecl, factoryName)
        return RouteClass(
            className = className,
            pathName = pathDecl.toClassName(),
            viewName = fragment.toClassName(),
            componentCntrl = componentDecl != null,
            componentName = componentDecl?.toClassName(),
            isCompose = false,
            routeType = fragment.routeType(),
            sourceFile = fragment.containingFile,
            uri = annotation.value("uri") ?: "",
            presentation = annotation.enumValue("presentation", Presentation.Push),
            singleTop = annotation.enumValue("singleTop", SingleTop.None),
            tabsProperties = fragment.tabsProperties(),
            animationClass = annotation.className("animation")?.takeUnless { it.isEmptyAnimationMarker() },
            chainPaths = fragment.annotationKClassList(CHAIN_ANNOTATION, "closeItems"),
            creatingInjector = false,
            middlewares = emptyList())
    }

    private fun generateController(
        fragment: KSClassDeclaration,
        pathDecl: KSClassDeclaration,
        vmDecl: KSClassDeclaration?,
        modelProviderDecl: KSClassDeclaration?,
        componentDecl: KSClassDeclaration?,
        factoryName: String?): ClassName
    {
        val pathClass = pathDecl.toClassName()
        val fragmentClass = fragment.toClassName()
        val vmClass = vmDecl?.toClassName()
        val modelProviderClass = if (vmDecl == null)
            null
        else
            modelProviderDecl?.toClassName() ?: ClassName("$MAIN_ROUTER_PACK.fragment", "FragmentViewModelProvider")
        val componentClass = componentDecl?.toClassName()

        val classNameImpl = "${fragment.simpleName.asString()}_${pathClass.canonicalName.sanitizeForClassName()}_RouteController"
        val classBuilder = TypeSpec.classBuilder(classNameImpl)
            .superclass(superClass(pathClass, fragmentClass, vmClass, modelProviderClass, componentClass))

        classBuilder.addFunction(createViewFunction(fragmentClass, pathClass, pathDecl, factoryName))

        if (vmClass != null && modelProviderClass != null)
        {
            classBuilder.addFunction(FunSpec.builder(CREATE_VIEWMODEL)
                .addModifiers(KModifier.OVERRIDE, KModifier.PROTECTED)
                .addParameter("modelProvider", modelProviderClass)
                .addParameter("path", pathClass)
                .returns(vmClass)
                .addStatement("return modelProvider.getViewModel()")
                .build())
        }

        if (componentClass != null)
        {
            val targetName = if (vmClass == null) "view" else "vm"
            val targetType = if (vmClass == null) fragmentClass else vmClass
            classBuilder.addFunction(FunSpec.builder(INJECT)
                .addModifiers(KModifier.OVERRIDE, KModifier.PROTECTED)
                .addParameter(targetName, targetType)
                .addParameter("component", componentClass)
                .addStatement("component.inject($targetName)")
                .build())
        }

        FileSpec.builder(fragment.packageName.asString(), classNameImpl)
            .addType(classBuilder.build())
            .build()
            .writeTo(codeGenerator, Dependencies(aggregating = false, sources = listOfNotNull(fragment.containingFile).toTypedArray()))

        return ClassName(fragment.packageName.asString(), classNameImpl)
    }

    private fun superClass(
        pathClass: ClassName,
        fragmentClass: ClassName,
        vmClass: ClassName?,
        modelProviderClass: ClassName?,
        componentClass: ClassName?) =
        when
        {
            vmClass != null && modelProviderClass != null && componentClass != null ->
                ClassName(CONTROLLERS_PACK, "RouteControllerVMC")
                    .parameterizedBy(pathClass, vmClass, modelProviderClass, fragmentClass, componentClass)
            vmClass != null && modelProviderClass != null ->
                ClassName(CONTROLLERS_PACK, "RouteControllerVM")
                    .parameterizedBy(pathClass, vmClass, modelProviderClass, fragmentClass)
            componentClass != null ->
                ClassName(CONTROLLERS_PACK, "RouteControllerC")
                    .parameterizedBy(pathClass, fragmentClass, componentClass)
            else ->
                ClassName(CONTROLLERS_PACK, "RouteController")
                    .parameterizedBy(pathClass, fragmentClass)
        }

    private fun createViewFunction(
        fragmentClass: ClassName,
        pathClass: ClassName,
        pathDecl: KSClassDeclaration,
        factoryName: String?): FunSpec
    {
        val builder = FunSpec.builder(CREATE_VIEW)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("path", pathClass)
            .returns(fragmentClass)

        if (factoryName != null)
        {
            builder.addStatement("return %T.%L(path)", fragmentClass, factoryName)
        }
        else
        {
            builder.addStatement("val arguments = %T()", ClassName("android.os", "Bundle"))
            pathConstructorProperties(pathDecl).forEach {
                val type = it.type.resolve()
                if (type.isParcelable())
                    builder.addStatement("arguments.putParcelable(%S, path.%L)", it.name!!.asString(), it.name!!.asString())
                else
                    builder.addStatement("arguments.putSerializable(%S, path.%L as %T?)", it.name!!.asString(), it.name!!.asString(), java.io.Serializable::class)
            }
            builder.addStatement("val fragment = %T()", fragmentClass)
            builder.addStatement("(fragment as %T).arguments = arguments", ClassName("androidx.fragment.app", "Fragment"))
            builder.addStatement("return fragment")
        }

        return builder.build()
    }

    private fun validateBundlePath(pathDecl: KSClassDeclaration, fragment: KSClassDeclaration): Boolean
    {
        if (pathDecl.primaryConstructor == null)
        {
            reporter.fail(
                "Router KSP: @RouterFragmentRoute path ${pathDecl.qualifiedName?.asString()} must have a primary constructor. Use @RouterFragment factory mode for custom path mapping.",
                fragment)
            return false
        }

        return pathConstructorProperties(pathDecl).all {
            val name = it.name?.asString()
            if (name == null || (!it.isVal && !it.isVar))
            {
                reporter.fail(
                    "Router KSP: @RouterFragmentRoute path ${pathDecl.qualifiedName?.asString()} constructor parameter must be a val/var property. Use @RouterFragment factory mode for custom path mapping.",
                    fragment)
                false
            }
            else if (!it.type.resolve().isBundleSupported())
            {
                reporter.fail(
                    "Router KSP: @RouterFragmentRoute cannot put ${pathDecl.simpleName.asString()}.$name into Bundle. Supported values must be Serializable or Parcelable. Use @RouterFragment factory mode for custom path mapping.",
                    fragment)
                false
            }
            else
            {
                true
            }
        }
    }

    private fun validateFactory(fragment: KSClassDeclaration, pathDecl: KSClassDeclaration, factoryName: String): Boolean
    {
        val factory = fragment.getDeclaredFunctions().firstOrNull { it.matchesFactory(factoryName, pathDecl, fragment) }
            ?: fragment.declarations
                .filterIsInstance<KSClassDeclaration>()
                .firstOrNull { it.isCompanionObject }
                ?.getDeclaredFunctions()
                ?.firstOrNull { it.matchesFactory(factoryName, pathDecl, fragment) }

        if (factory != null)
            return true

        reporter.fail(
            "Router KSP: ${fragment.qualifiedName?.asString()} uses @RouterFragment(path = ${pathDecl.simpleName.asString()}::class), but factory method ${fragment.simpleName.asString()}.$factoryName(${pathDecl.simpleName.asString()}): ${fragment.simpleName.asString()} was not found.",
            fragment)
        return false
    }

    private fun KSFunctionDeclaration.matchesFactory(
        factoryName: String,
        pathDecl: KSClassDeclaration,
        fragment: KSClassDeclaration): Boolean
    {
        if (simpleName.asString() != factoryName || parameters.size != 1)
            return false
        val paramDecl = parameters[0].type.resolve().declaration as? KSClassDeclaration ?: return false
        val returnDecl = returnType?.resolve()?.declaration as? KSClassDeclaration ?: return false
        return paramDecl.qualifiedName?.asString() == pathDecl.qualifiedName?.asString() &&
            returnDecl.qualifiedName?.asString() == fragment.qualifiedName?.asString()
    }

    private fun pathConstructorProperties(pathDecl: KSClassDeclaration): List<KSValueParameter> =
        pathDecl.primaryConstructor?.parameters.orEmpty()

    private fun String.sanitizeForClassName(): String =
        replace(Regex("[^A-Za-z0-9_]"), "_")

    private enum class FragmentFactory
    {
        Bundle,
        Method
    }

}
