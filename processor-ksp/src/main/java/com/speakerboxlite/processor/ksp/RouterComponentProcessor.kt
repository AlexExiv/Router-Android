package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.SingleTop
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.writeTo

internal class RouterComponentProcessor(private val codeGenerator: CodeGenerator)
{
    fun generate(routerApp: RouterAppInfo, routes: List<RouteClass>, middlewares: List<MiddlewareController>, hadComponent: Boolean)
    {
        val fileName = "RouterComponentImpl"
        val classBuilder = TypeSpec.classBuilder(fileName)
            .addSuperinterface(ClassName(MAIN_ROUTER_PACK, "RouterComponent"))

        classBuilder.addProperty(PropertySpec
            .builder("routeManager", ClassName(MAIN_ROUTER_PACK, "RouteManager"))
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .initializer("%T()", ClassName(MAIN_ROUTER_PACK, "RouteManagerImpl"))
            .build())

        classBuilder.addProperty(PropertySpec
            .builder("resultManager", ClassName("$MAIN_ROUTER_PACK.result", "ResultManager"))
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .initializer("%T()", ClassName("$MAIN_ROUTER_PACK.result", "ResultManagerImpl"))
            .build())

        classBuilder.addProperty(PropertySpec
            .builder("routerManager", ClassName(MAIN_ROUTER_PACK, "RouterManager"))
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
            .initializer("%T()", ClassName(MAIN_ROUTER_PACK, "RouterManagerImpl"))
            .build())

        val componentProviderLazy = CodeBlock.builder()
            .beginControlFlow("lazy(mode = %T.SYNCHRONIZED)", LazyThreadSafetyMode::class.asTypeName())
            .addStatement("%T(component)", ClassName(MAIN_ROUTER_PACK, "ComponentProviderImpl"))
            .endControlFlow()
            .build()

        classBuilder.addProperty(PropertySpec
            .builder("componentProvider", ClassName(MAIN_ROUTER_PACK, "ComponentProvider"))
            .addModifiers(KModifier.PUBLIC)
            .delegate(componentProviderLazy)
            .build())

        classBuilder.addProperty(PropertySpec
            .builder("component", ANY)
            .addModifiers(KModifier.PRIVATE, KModifier.LATEINIT)
            .mutable(true)
            .build())

        classBuilder.addProperty(PropertySpec
            .builder("startPath", ClassName(MAIN_ROUTER_PACK, "RoutePath"))
            .addModifiers(KModifier.PRIVATE, KModifier.LATEINIT)
            .mutable(true)
            .build())

        val initBuilder = FunSpec.builder("initialize")
            .addModifiers(KModifier.PUBLIC)
            .addParameter("startPath", ClassName(MAIN_ROUTER_PACK, "RoutePath"))
            .addParameter("animationFactory", ClassName(CONTROLLERS_PACK, "AnimationControllerFactory").copy(nullable = true))
            .addStatement("this.startPath = startPath")
            .addStatement("")

        addMiddlewares(initBuilder, middlewares)
        routes.forEach { addRoute(initBuilder, it) }
        finishInitialize(classBuilder, initBuilder, hadComponent)

        val sources = (routes.mapNotNull { it.sourceFile } + middlewares.mapNotNull { it.sourceFile } + listOfNotNull(routerApp.sourceFile)).distinct()
        FileSpec.builder(routerApp.packageName, fileName)
            .addType(classBuilder.build())
            .build()
            .writeTo(codeGenerator, Dependencies(aggregating = true, sources = sources.toTypedArray()))
    }

    private fun addMiddlewares(initBuilder: FunSpec.Builder, middlewares: List<MiddlewareController>)
    {
        middlewares.forEach {
            initBuilder.addStatement("val ${it.varName} = %T()", it.className)
            if (it.hasComponent)
                initBuilder.addStatement("${it.varName}.onInject(component)")
        }
        initBuilder.addStatement("")
    }

    private fun addRoute(initBuilder: FunSpec.Builder, route: RouteClass)
    {
        val valName = route.className.simpleName.lowercase()
        initBuilder.addStatement("val $valName = %T()", route.className)
        initBuilder.addStatement("routeManager.register($valName)")
        initBuilder.addStatement("${valName}.pathClass = %T::class", route.pathName)

        if (route.componentName != null)
            initBuilder.addStatement("${valName}.componentClass = %T::class", route.componentName)

        if (route.componentCntrl)
            initBuilder.addStatement("${valName}.onInject(component)")

        if (route.isCompose)
            initBuilder.addStatement("${valName}.isCompose = true")

        initBuilder.addStatement("${valName}.routeType = %T.%L", ClassName("$MAIN_ROUTER_PACK.annotations", "RouteType"), route.routeType.toString())
        initBuilder.addStatement("${valName}.middlewares = listOf(${route.middlewares.map { it.varName }.joinToString(", ")})")

        if (route.uri.isNotEmpty())
            initBuilder.addStatement("${valName}.pattern = %T(%S)", ClassName("$MAIN_ROUTER_PACK.pattern", "UrlPattern"), route.uri)

        if (route.presentation != Presentation.Push)
            initBuilder.addStatement("${valName}.preferredPresentation = %T.%L", ClassName("$MAIN_ROUTER_PACK.annotations", "Presentation"), route.presentation.toString())

        if (route.singleTop != SingleTop.None)
            initBuilder.addStatement("${valName}.singleTop = %T.%L", ClassName("$MAIN_ROUTER_PACK.annotations", "SingleTop"), route.singleTop.toString())

        if (route.tabsProperties != null)
        {
            val tabProps = route.tabsProperties
            initBuilder.addStatement(
                "${valName}.tabProps = %T(%L, %L, %T.%L)",
                ClassName(CONTROLLERS_PACK, "TabsProperties"),
                tabProps.tabRouteInParent,
                tabProps.backToFirst,
                ClassName("$MAIN_ROUTER_PACK.annotations", "TabUnique"),
                tabProps.tabUnique.toString())
        }

        if (route.animationClass != null)
            initBuilder.addStatement("${valName}.preferredAnimationController = %T() as %T", route.animationClass, ClassName(CONTROLLERS_PACK, "AnimationController"))

        initBuilder.addStatement("${valName}.animationControllerFactory = animationFactory")

        if (route.chainPaths.isNotEmpty())
        {
            val chains = route.chainPaths
                .map { CodeBlock.of("%T::class", it) }
                .joinToCode(prefix = "listOf(", separator = ",", suffix = ")")
            initBuilder.addStatement("${valName}.chainPaths = $chains")
        }

        if (route.creatingInjector)
            initBuilder.addStatement("${valName}.creatingInjector = true")

        initBuilder.addStatement("")
    }

    private fun finishInitialize(classBuilder: TypeSpec.Builder, initBuilder: FunSpec.Builder, hadComponent: Boolean)
    {
        if (hadComponent)
        {
            initBuilder.addParameter("component", ANY)
            initBuilder.addStatement("this.component = component")
        }

        val routerClass = if (hadComponent)
            ClassName(MAIN_ROUTER_PACK, "RouterInjector")
        else
            ClassName(MAIN_ROUTER_PACK, "RouterSimple")

        initBuilder.addStatement("routerManager[%M] = startRouter", MemberName(MAIN_ROUTER_PACK, "START_ACTIVITY_KEY"))

        val startRouterLazy = CodeBlock.builder()
            .beginControlFlow("lazy(mode = %T.SYNCHRONIZED)", LazyThreadSafetyMode::class.asTypeName())
            .addStatement("val router = %T(null, null, routeManager, routerManager, resultManager${if (hadComponent) ", componentProvider" else ""})", routerClass)
            .addStatement("router")
            .endControlFlow()
            .build()

        classBuilder.addProperty(PropertySpec
            .builder("startRouter", ClassName(MAIN_ROUTER_PACK, "Router"))
            .addModifiers(KModifier.PUBLIC)
            .delegate(startRouterLazy)
            .build())

        initBuilder.addStatement("startRouter.route(startPath, %T.%L)", ClassName("$MAIN_ROUTER_PACK.annotations", "Presentation"), Presentation.Push.toString())
        classBuilder.addFunction(initBuilder.build())
    }
}
