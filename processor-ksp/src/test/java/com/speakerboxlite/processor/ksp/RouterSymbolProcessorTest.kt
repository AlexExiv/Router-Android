package com.speakerboxlite.processor.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

@OptIn(ExperimentalCompilerApi::class)
class RouterSymbolProcessorTest
{
    @Test
    fun generatesRouterComponentAndControllerImpl()
    {
        val run = compile(
            SourceFile.kotlin("Routes.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.annotations.Presentation
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.controllers.RouteController

                @RouterApp
                class App

                class StartPath: RoutePath
                class ProductPath: RoutePath

                class ProductView: View {
                    override var viewKey: String = ""
                }

                @Route(uri = "/product/{id}", presentation = Presentation.Modal)
                abstract class ProductRouteController: RouteController<ProductPath, ProductView>()
            """.trimIndent()))

        val result = run.result
        assertEquals(result.messages, KotlinCompilation.ExitCode.OK, result.exitCode)
        assertTrue(run.generatedFile("test", "ProductRouteController_IMP.kt").readText().contains("class ProductRouteController_IMP"))
        assertTrue(run.generatedFile("test", "RouterComponentImpl.kt").readText().contains("routeManager.register(productroutecontroller_imp)"))
    }

    @Test
    fun reportsMissingRouterApp()
    {
        val result = compile(
            SourceFile.kotlin("Routes.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.controllers.RouteController

                class ProductPath: RoutePath
                class ProductView: View {
                    override var viewKey: String = ""
                }

                @Route
                abstract class ProductRouteController: RouteController<ProductPath, ProductView>()
            """.trimIndent())).result

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Add @RouterApp to your Application class"))
    }

    @Test
    fun supportsTypealiasComponentControllers()
    {
        val run = compile(
            SourceFile.kotlin("ComponentRoute.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.ViewModel
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.controllers.Component
                import com.speakerboxlite.router.controllers.RouteControllerVMC
                import com.speakerboxlite.router.controllers.RouterModelProvider

                @RouterApp
                class App

                class DetailsPath: RoutePath
                class DetailsView: View {
                    override var viewKey: String = ""
                }
                class DetailsVm: ViewModel
                class ModelProvider: RouterModelProvider
                class AppComponent: Component {
                    fun inject(vm: DetailsVm) {}
                }

                typealias AppRouteController<P, VM, V> = RouteControllerVMC<P, VM, ModelProvider, V, AppComponent>

                @Route
                abstract class DetailsRouteController: AppRouteController<DetailsPath, DetailsVm, DetailsView>()
            """.trimIndent()))

        assertEquals(run.result.messages, KotlinCompilation.ExitCode.OK, run.result.exitCode)
        val generatedController = run.generatedFile("test", "DetailsRouteController_IMP.kt").readText()
        assertTrue(generatedController.contains("component.inject(vm)"))
        assertTrue(run.generatedFile("test", "RouterComponentImpl.kt").readText().contains("RouterInjector"))
    }

    private fun compile(vararg sources: SourceFile): CompilationRun
    {
        val workingDir = createTempDirectory("router-ksp-test").toFile()
        val result = KotlinCompilation().apply {
            inheritClassPath = true
            this.workingDir = workingDir
            symbolProcessorProviders = listOf(RouterSymbolProcessorProvider())
            this.sources = routerStubs() + sources
            messageOutputStream = System.out
        }.compile()
        return CompilationRun(result, workingDir)
    }

    private data class CompilationRun(
        val result: KotlinCompilation.Result,
        val workingDir: File)
    {
        fun generatedFile(packageName: String, fileName: String): File =
            workingDir
            .resolve("ksp/sources/kotlin/${packageName.replace('.', '/')}/$fileName")
            .takeIf { it.exists() }
            ?: workingDir.walkTopDown().first { it.name == fileName }
    }

    private fun routerStubs(): List<SourceFile> = listOf(
        SourceFile.kotlin("RouterStubs.kt", """
            package com.speakerboxlite.router

            import com.speakerboxlite.router.annotations.Presentation
            import com.speakerboxlite.router.controllers.RouteControllerInterface
            import com.speakerboxlite.router.result.ResultManager
            import java.io.Serializable

            const val START_ACTIVITY_KEY = "start"

            interface RoutePath: Serializable
            interface View { var viewKey: String }
            interface ViewModel
            interface Router
            interface RouterComponent {
                val routeManager: RouteManager
                val routerManager: RouterManager
            }
            interface RouteManager {
                fun <Path: RoutePath> register(route: RouteControllerInterface<Path, *>)
            }
            class RouteManagerImpl: RouteManager {
                override fun <Path: RoutePath> register(route: RouteControllerInterface<Path, *>) {}
            }
            interface RouterManager {
                operator fun set(key: String, value: Router?)
            }
            class RouterManagerImpl: RouterManager {
                override fun set(key: String, value: Router?) {}
            }
            interface ComponentProvider
            class ComponentProviderImpl(component: Any): ComponentProvider
            open class RouterSimple(callerKey: String?, parent: RouterSimple?, routeManager: RouteManager, routerManager: RouterManager, resultManager: ResultManager): Router {
                fun route(path: RoutePath, presentation: Presentation): Router? = this
            }
            class RouterInjector(callerKey: String?, parent: RouterSimple?, routeManager: RouteManager, routerManager: RouterManager, resultManager: ResultManager, componentProvider: ComponentProvider): RouterSimple(callerKey, parent, routeManager, routerManager, resultManager)
        """.trimIndent()),
        SourceFile.kotlin("ControllerStubs.kt", """
            package com.speakerboxlite.router.controllers

            import com.speakerboxlite.router.RoutePath
            import com.speakerboxlite.router.Router
            import com.speakerboxlite.router.View
            import com.speakerboxlite.router.ViewModel
            import com.speakerboxlite.router.annotations.Presentation
            import com.speakerboxlite.router.annotations.RouteType
            import com.speakerboxlite.router.annotations.SingleTop
            import com.speakerboxlite.router.annotations.TabUnique
            import kotlin.reflect.KClass

            interface AnimationController
            fun interface AnimationControllerFactory {
                fun onCreate(presentation: Presentation?, view: View): AnimationController?
            }
            interface MiddlewareController
            interface MiddlewareControllerComponent: MiddlewareController {
                fun onInject(component: Any)
            }
            interface Component
            interface RouterModelProvider {
                fun <VM: ViewModel> getViewModel(): VM = error("stub")
            }
            data class TabsProperties(val tabRouteInParent: Boolean, val backToFirst: Boolean, val tabUnique: TabUnique)
            data class RouteParams<Path: RoutePath>(val path: Path)
            typealias RouteParamsGen = RouteParams<RoutePath>

            interface RouteControllerInterface<Path: RoutePath, V: View> {
                var pathClass: KClass<Path>
                var singleTop: SingleTop
                var creatingInjector: Boolean
                var preferredPresentation: Presentation
                var isCompose: Boolean
                var routeType: RouteType
                var tabProps: TabsProperties?
                var middlewares: List<MiddlewareController>
                var pattern: Any?
                var preferredAnimationController: AnimationController?
                var animationControllerFactory: AnimationControllerFactory?
                var chainPaths: List<KClass<*>>
            }

            abstract class RouteController<Path: RoutePath, V: View>: RouteControllerInterface<Path, V> {
                override lateinit var pathClass: KClass<Path>
                override var singleTop: SingleTop = SingleTop.None
                override var creatingInjector: Boolean = false
                override var preferredPresentation: Presentation = Presentation.Push
                override var isCompose: Boolean = false
                override var routeType: RouteType = RouteType.Simple
                override var tabProps: TabsProperties? = null
                override var middlewares: List<MiddlewareController> = listOf()
                override var pattern: Any? = null
                override var preferredAnimationController: AnimationController? = null
                override var animationControllerFactory: AnimationControllerFactory? = null
                override var chainPaths: List<KClass<*>> = listOf()
                abstract fun onCreateView(path: Path): V
            }

            abstract class RouteControllerVMC<Path: RoutePath, VM: ViewModel, ModelProvider: RouterModelProvider, V: View, C: Component>: RouteController<Path, V>() {
                protected abstract fun onCreateViewModel(modelProvider: ModelProvider, path: Path): VM
                protected abstract fun onInject(vm: VM, component: C)
            }
        """.trimIndent()),
        SourceFile.kotlin("ResultStubs.kt", """
            package com.speakerboxlite.router.result

            interface ResultManager
            class ResultManagerImpl: ResultManager
        """.trimIndent()),
        SourceFile.kotlin("PatternStubs.kt", """
            package com.speakerboxlite.router.pattern

            class UrlPattern(val value: String)
        """.trimIndent()))
}
