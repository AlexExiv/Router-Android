package com.speakerboxlite.processor

import com.google.auto.service.AutoService
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.Route
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import java.lang.Exception
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor()
{
    override fun getSupportedAnnotationTypes(): MutableSet<String>
    {
        return mutableSetOf(Route::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean
    {
        try
        {
            if (roundEnv.rootElements.isEmpty())
                return false

            val elements = roundEnv.getElementsAnnotatedWith(Route::class.java)
            if (elements.isEmpty())
                return false

            val processorMiddleware = MiddlewareProcessor(roundEnv)
            val middlewares = processorMiddleware.prepareMiddlewares()

            val pack = processingEnv.elementUtils.getPackageOf(roundEnv.rootElements.first()).toString()

            val fileName = "RouterComponentImpl"
            val fileBuilder = FileSpec.builder(pack, fileName)
            val classBuilder = TypeSpec.classBuilder(fileName)

            val routeManager = PropertySpec
                .builder("routeManager", ClassName(MAIN_ROUTER_PACK, "RouteManager"))
                .addModifiers(listOf(KModifier.PUBLIC))
                .initializer("%T()", ClassName(MAIN_ROUTER_PACK, "RouteManagerImpl"))
                .build()

            val resultManager = PropertySpec
                .builder("resultManager", ClassName("${MAIN_ROUTER_PACK}.result", "ResultManager"))
                .addModifiers(listOf(KModifier.PUBLIC))
                .initializer("%T()", ClassName("${MAIN_ROUTER_PACK}.result", "ResultManagerImpl"))
                .build()

            val routerManager = PropertySpec
                .builder("routerManager", ClassName(MAIN_ROUTER_PACK, "RouterManager"))
                .addModifiers(listOf(KModifier.PUBLIC))
                .initializer("%T()", ClassName(MAIN_ROUTER_PACK, "RouterManagerImpl"))
                .build()

            val componentProviderLazy = CodeBlock.builder()
                .beginControlFlow("lazy(mode = %T.SYNCHRONIZED)", LazyThreadSafetyMode::class.asTypeName())
                .addStatement("%T(component)", ClassName(MAIN_ROUTER_PACK, "ComponentProviderImpl"))
                .endControlFlow()
                .build()

            val componentProvider = PropertySpec
                .builder("componentProvider", ClassName(MAIN_ROUTER_PACK, "ComponentProvider"))
                .addModifiers(listOf(KModifier.PUBLIC))
                .delegate(componentProviderLazy)
                .build()

            val startPath = PropertySpec
                .builder("startPath", ClassName(MAIN_ROUTER_PACK, "RoutePath"))
                .addModifiers(listOf(KModifier.PRIVATE, KModifier.LATEINIT))
                .mutable(true)
                .build()

            val component = PropertySpec
                .builder("component", ANY)
                .addModifiers(listOf(KModifier.PRIVATE, KModifier.LATEINIT))
                .mutable(true)
                .build()

            classBuilder.addProperty(routeManager)
            classBuilder.addProperty(resultManager)
            classBuilder.addProperty(routerManager)
            classBuilder.addProperty(componentProvider)
            classBuilder.addProperty(component)
            classBuilder.addProperty(startPath)

            val initBuilder = FunSpec.builder("initialize")
            initBuilder.addModifiers(listOf(KModifier.PUBLIC))
            initBuilder.addParameter("startPath", ClassName(MAIN_ROUTER_PACK, "RoutePath"))

            val animClass = ClassName(CONTROLLERS_PACK, "AnyAnimationController")
            initBuilder.addParameter("animation", animClass.copy(nullable = true))

            initBuilder.addStatement("this.startPath = startPath")
            initBuilder.addStatement("")

            val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!

            val processorManager = RouteControllerProcessorManager()
            processorManager.register(RouteControllerVMProcessor(processingEnv, kaptKotlinGeneratedDir, MAIN_ROUTER_PACK))
            processorManager.register(RouteControllerVMCProcessor(processingEnv, kaptKotlinGeneratedDir, MAIN_ROUTER_PACK))
            processorManager.register(RouteControllerCProcessor(processingEnv, kaptKotlinGeneratedDir, MAIN_ROUTER_PACK))
            processorManager.register(RouteControllerProcessor(processingEnv, kaptKotlinGeneratedDir, MAIN_ROUTER_PACK))

            middlewares.forEach {
                initBuilder.addStatement("val ${it.varName} = %T()", it.className)
                if (it.typeElement.hasParent("MiddlewareControllerComponent", true))
                    initBuilder.addStatement("${it.varName}.onInject(component)")
            }
            initBuilder.addStatement("")

            elements.forEach {
                if (it.kind != ElementKind.CLASS)
                {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                    return true
                }

                processAnnotation(it, initBuilder, processorManager, processorMiddleware)
            }

            val routerClass = if (processorManager.hadComponent)
            {
                initBuilder.addParameter("component", ANY)
                initBuilder.addStatement("this.component = component")
                ClassName(MAIN_ROUTER_PACK, "RouterInjector")
            }
            else
                ClassName(MAIN_ROUTER_PACK, "RouterSimple")

            initBuilder.addStatement("routerManager[%M] = startRouter", MemberName(MAIN_ROUTER_PACK, "START_ACTIVITY_KEY"))

            val startRouterLazy = CodeBlock.builder()
                .beginControlFlow("lazy(mode = %T.SYNCHRONIZED)", LazyThreadSafetyMode::class.asTypeName())
                .addStatement("val router = %T(null, null, routeManager, routerManager, resultManager, componentProvider)", routerClass)
                .addStatement("router")
                .endControlFlow()
                .build()

            val startRouter = PropertySpec
                .builder("startRouter", ClassName(MAIN_ROUTER_PACK, "Router"))
                .addModifiers(listOf(KModifier.PUBLIC))
                .delegate(startRouterLazy)
                .build()

            classBuilder.addProperty(startRouter)

            val presentationEnum = ClassName("$MAIN_ROUTER_PACK.annotations", "Presentation")
            initBuilder.addStatement("startRouter.route(startPath, %T.%L)", presentationEnum, Presentation.Push.toString())

            initBuilder.build()
            classBuilder.addFunction(initBuilder.build())

            val file = fileBuilder.addType(classBuilder.build()).build()
            file.writeTo(File(kaptKotlinGeneratedDir))
        }
        catch (e: Exception)
        {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.stackTraceToString())
        }

        return false
    }

    private fun processAnnotation(element: Element, initBuilder: FunSpec.Builder, processorManager: RouteControllerProcessorManager,
                                  middlewareProcessor: MiddlewareProcessor)
    {
        val elementClass = element as TypeElement
        val routeClass = processorManager.createClass(elementClass)
        val classType = routeClass.className
        val valName = classType.simpleName.lowercase()

        initBuilder.addStatement("val $valName = %T()", classType)
        initBuilder.addStatement("routeManager.register($valName)")

        initBuilder.addStatement("${valName}.pathClass = %T::class", routeClass.pathName)
        if (routeClass.componentName != null)
            initBuilder.addStatement("${valName}.componentClass = %T::class", routeClass.componentName)

        val middlewares = middlewareProcessor.buildMiddlewares(elementClass)
        initBuilder.addStatement("${valName}.middlewares = listOf(${middlewares.map { it.varName }.joinToString(", ")})")

        val annotation = element.getAnnotation(Route::class.java)
        if (annotation.uri.isNotEmpty())
        {
            val patternClass = ClassName("$MAIN_ROUTER_PACK.pattern", "UrlPattern")
            initBuilder.addStatement("${valName}.pattern = %T(%S)", patternClass, annotation.uri)
        }

        if (annotation.presentation != Presentation.Push)
        {
            val presentationEnum = ClassName("$MAIN_ROUTER_PACK.annotations", "Presentation")
            initBuilder.addStatement("${valName}.preferredPresentation = %T.%L", presentationEnum, annotation.presentation.toString())
        }

        if (annotation.singleTop)
            initBuilder.addStatement("${valName}.singleTop = true")

        val animationMirror = try
            {
                annotation.animation as TypeMirror
            }
            catch (e: MirroredTypeException)
            {
                e.typeMirror
            }

        val animDecl = animationMirror as? DeclaredType
        if (animDecl != null && animDecl.asElement()?.simpleName?.contentEquals(Nothing::class.simpleName) == false)
        {
            val typeE = animDecl.asElement() as TypeElement
/*
            if (typeE.superclasses.firstOrNull { it.qualifiedName == "${CONTROLLERS_PACK}.AnimationController" } == null)
                throw IllegalArgumentException("Animation must be a subclass of AnimationController")
*/
            val animClass = typeE.asClassName()
            val anyAnimClass = ClassName(CONTROLLERS_PACK, "AnyAnimationController")
            initBuilder.addStatement("${valName}.preferredAnimationController = %T() as %T", animClass, anyAnimClass)
        }
        else
            initBuilder.addStatement("${valName}.preferredAnimationController = animation")

        val names = elementClass.getExecutables().map { it.simpleName.toString() }
        if (names.contains(CREATE_INJECTOR))
            initBuilder.addStatement("${valName}.creatingInjector = true")

        initBuilder.addStatement("")
    }

    companion object
    {
        val MAIN_ROUTER_PACK = "com.speakerboxlite.router"
        val CONTROLLERS_PACK = "com.speakerboxlite.router.controllers"

        val MIDDLEWARE_CLASSES = listOf("MiddlewareController", "MiddlewareControllerComponent")

        const val CREATE_INJECTOR = "onCreateInjector"

        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}

fun Element.getPack(processingEnv: ProcessingEnvironment): String = processingEnv.elementUtils.getPackageOf(this).toString()

fun TypeElement.getExecutables(): List<ExecutableElement>
{
    val methods = mutableListOf<ExecutableElement>()

    for (methodDeclaration in enclosedElements)
        if (methodDeclaration is ExecutableElement)
            methods.add(methodDeclaration)

    return methods
}

fun TypeElement.hasParent(name: String, interfaces: Boolean = false): Boolean
{
    val sc = superclass as? DeclaredType ?: return false
    val sd = sc.asElement() as TypeElement

    if (sd.simpleName.contentEquals(name) || (interfaces && this.interfaces.firstOrNull { (it as? DeclaredType)?.asElement()?.simpleName?.contentEquals(name) == true } != null))
        return true

    return sd.hasParent(name)
}

fun TypeElement.hasAnyParent(names: List<String>, interfaces: Boolean = false): Boolean =
    names.firstOrNull { hasParent(it, interfaces) } != null

fun TypeElement.collectAnnotations(): List<TypeElement>
{
    val anns = annotationMirrors
        .mapNotNull { it.annotationType.asElement() as? TypeElement }

    val sc = superclass as? DeclaredType ?: return anns
    val sd = sc.asElement() as TypeElement

    return anns + sd.collectAnnotations()
}