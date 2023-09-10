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

            val pack = processingEnv.elementUtils.getPackageOf(roundEnv.rootElements.first()).toString()

            val fileName = "RouterComponentImpl"
            val fileBuilder = FileSpec.builder(pack, fileName)
            val classBuilder = TypeSpec.classBuilder(fileName)

            classBuilder.addSuperinterface(ClassName(MAIN_ROUTER_PACK, "RouterComponent"))

            val routeManager = PropertySpec
                .builder("routeManager", ClassName(MAIN_ROUTER_PACK, "RouteManager"))
                .addModifiers(listOf(KModifier.PUBLIC, KModifier.OVERRIDE))
                .initializer("%T()", ClassName(MAIN_ROUTER_PACK, "RouteManagerImpl"))
                .build()

            val resultManager = PropertySpec
                .builder("resultManager", ClassName("${MAIN_ROUTER_PACK}.result", "ResultManager"))
                .addModifiers(listOf(KModifier.PUBLIC, KModifier.OVERRIDE))
                .initializer("%T()", ClassName("${MAIN_ROUTER_PACK}.result", "ResultManagerImpl"))
                .build()

            val routerManager = PropertySpec
                .builder("routerManager", ClassName(MAIN_ROUTER_PACK, "RouterManager"))
                .addModifiers(listOf(KModifier.PUBLIC, KModifier.OVERRIDE))
                .initializer("%T()", ClassName(MAIN_ROUTER_PACK, "RouterManagerImpl"))
                .build()

            val startRouterLazy = CodeBlock.builder()
                .beginControlFlow("lazy(mode = %T.SYNCHRONIZED)", LazyThreadSafetyMode::class.asTypeName())
                .addStatement("val router = %T(null, null, routeManager, routerManager, resultManager, component)", ClassName(MAIN_ROUTER_PACK, "RouterSimple"))
                .addStatement("router")
                .endControlFlow()
                .build()

            val startRouter = PropertySpec
                .builder("startRouter", ClassName(MAIN_ROUTER_PACK, "Router"))
                .addModifiers(listOf(KModifier.PUBLIC, KModifier.OVERRIDE))
                .delegate(startRouterLazy)
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
            classBuilder.addProperty(startRouter)
            classBuilder.addProperty(component)
            classBuilder.addProperty(startPath)

            val initBuilder = FunSpec.builder("initialize")
            initBuilder.addModifiers(listOf(KModifier.PUBLIC, KModifier.OVERRIDE))
            initBuilder.addParameter("component", ANY)
            initBuilder.addParameter("startPath", ClassName(MAIN_ROUTER_PACK, "RoutePath"))
            initBuilder.addStatement("this.component = component")
            initBuilder.addStatement("this.startPath = startPath")
            initBuilder.addStatement("routerManager[%M] = startRouter", MemberName(MAIN_ROUTER_PACK, "START_ACTIVITY_KEY"))

            elements.forEach {
                if (it.kind != ElementKind.CLASS)
                {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                    return true
                }

                processAnnotation(it, initBuilder)
            }

            val presentationEnum = ClassName("$MAIN_ROUTER_PACK.annotations", "Presentation")
            initBuilder.addStatement("startRouter.route(startPath, %T.%L)", presentationEnum, Presentation.Push.toString())

            initBuilder.build()
            classBuilder.addFunction(initBuilder.build())

            val file = fileBuilder.addType(classBuilder.build()).build()
            val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            file.writeTo(File(kaptKotlinGeneratedDir))
        }
        catch (e: Exception)
        {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.stackTraceToString())
        }

        return false
    }

    private fun processAnnotation(element: Element, initBuilder: FunSpec.Builder)
    {
        val elementClass = element as TypeElement
        val classType = createClass(elementClass)
        val valName = classType.simpleName.lowercase()

        initBuilder.addStatement("val $valName = %T()", classType)
        initBuilder.addStatement("routeManager.register($valName)")

        val typeArguments = (element.superclass as DeclaredType).typeArguments as List<DeclaredType>
        val pathElement = typeArguments[PATH_INDEX].asElement()
        val pathElementPack = processingEnv.elementUtils.getPackageOf(pathElement).toString()
        val pathElementType = ClassName(pathElementPack, pathElement.simpleName.toString())
        initBuilder.addStatement("${valName}.pathClass = %T::class", pathElementType)

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
        {
            initBuilder.addStatement("${valName}.singleTop = true")
        }

        initBuilder.addStatement("")
    }

    private fun createClass(element: TypeElement): ClassName
    {
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()
        val className = element.simpleName.toString()

        val elementClassName = ClassName(pack, className)
        val names = element.getExecutables().map { it.simpleName.toString() }
        if (names.containsAll(REQUIRED_METHODS))
            return elementClassName

        val classNameImpl = "${className}_IMP"
        val classBuilder = TypeSpec.classBuilder(classNameImpl)
        val typeArguments = (element.superclass as DeclaredType).typeArguments as List<DeclaredType>

        if (!names.contains(CREATE_VIEW))
        {
            val viewElement = typeArguments[V_INDEX].asElement() as TypeElement
            val viewClass = ClassName(viewElement.getPack(processingEnv), viewElement.simpleName.toString())

            val func = FunSpec.builder(CREATE_VIEW)
            func.addModifiers(KModifier.OVERRIDE)
            //func.addModifiers(KModifier.PROTECTED)
            func.returns(viewClass)
            func.addStatement("return %T()", viewClass)
            classBuilder.addFunction(func.build())
        }

        if (!names.contains(CREATE_VIEWMODEL))
        {
            val viewElement = typeArguments[V_INDEX].asElement() as TypeElement
            val pathElement = typeArguments[PATH_INDEX].asElement() as TypeElement
            val vmElement = typeArguments[VM_INDEX].asElement() as TypeElement
/*
            val vmConstructors = vmElement.getExecutables().filter { it.simpleName.contentEquals("init") }
            vmConstructors[0].parameters[0].javaClass
*/
            val viewClass = ClassName(viewElement.getPack(processingEnv), viewElement.simpleName.toString())
            val pathClass = ClassName(pathElement.getPack(processingEnv), pathElement.simpleName.toString())
            val vmClass = ClassName(vmElement.getPack(processingEnv), vmElement.simpleName.toString())

            val getAndroidViewModel = MemberName(MAIN_ROUTER_PACK, "getAndroidViewModel")

            val func = FunSpec.builder(CREATE_VIEWMODEL)
            func.addModifiers(KModifier.OVERRIDE)
            func.addModifiers(KModifier.PROTECTED)
            func.addParameter("view", viewClass)
            func.addParameter("path", pathClass)
            func.returns(vmClass)
            func.addStatement("return view.%M()", getAndroidViewModel)
            classBuilder.addFunction(func.build())
        }

        if (!names.contains(INJECT))
        {
            val viewElement = typeArguments[V_INDEX].asElement() as TypeElement
            val vmElement = typeArguments[VM_INDEX].asElement() as TypeElement
            val componentElement = typeArguments[COMPONENT_INDEX].asElement() as TypeElement

            val viewClass = ClassName(viewElement.getPack(processingEnv), viewElement.simpleName.toString())
            val vmClass = ClassName(vmElement.getPack(processingEnv), vmElement.simpleName.toString())
            val componentClass = ClassName(componentElement.getPack(processingEnv), componentElement.simpleName.toString())

            val func = FunSpec.builder(INJECT)
            func.addModifiers(KModifier.OVERRIDE)
            func.addModifiers(KModifier.PROTECTED)
            func.addParameter("view", viewClass)
            func.addParameter("vm", vmClass)
            func.addParameter("component", componentClass)
            func.addStatement("component.inject(vm)")
            classBuilder.addFunction(func.build())
        }

        classBuilder.superclass(elementClassName)

        val file = FileSpec.builder(pack, classNameImpl)
            .addType(classBuilder.build())
            .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
        return ClassName(pack, classNameImpl)
    }

    fun getTypeArgumentWithParent(type: DeclaredType, name: String): DeclaredType?
    {
        for (t in type.typeArguments)
        {
            if (t is DeclaredType)
            {
                val generic = t.asElement() as TypeElement
                if (generic.interfaces.firstOrNull { (it as DeclaredType).asElement().simpleName.toString() == name } != null)
                    return t
            }
        }

        return null
    }

    companion object
    {
        val MAIN_ROUTER_PACK = "com.speakerboxlite.router"

        val PATH_INDEX = 0
        val VM_INDEX = 1
        val V_INDEX = 2
        val COMPONENT_INDEX = 3

        const val CREATE_VIEW = "onCreateView"
        const val CREATE_VIEWMODEL = "onCreateViewModel"
        const val INJECT = "onInject"

        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        val REQUIRED_METHODS = listOf(CREATE_VIEW, CREATE_VIEWMODEL, INJECT)
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