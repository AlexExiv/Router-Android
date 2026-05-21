package com.speakerboxlite.processor.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.useKsp2
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

    @Test
    fun generatesRepeatableFragmentRouteControllers()
    {
        val run = compile(
            SourceFile.kotlin("FragmentRoutes.kt", """
                package test

                import android.os.Bundle
                import androidx.fragment.app.Fragment
                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.Router
                import com.speakerboxlite.router.RouterLocal
                import com.speakerboxlite.router.annotations.Presentation
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.annotations.RouterFragmentRoute
                import com.speakerboxlite.router.fragment.ViewFragment
                import com.speakerboxlite.router.result.RouterResultProvider

                @RouterApp
                class App

                data class ProfilePath(val userId: String, val source: String): RoutePath
                data class ProfileDialogPath(val userId: String): RoutePath

                @RouterFragmentRoute(path = ProfilePath::class)
                @RouterFragmentRoute(path = ProfileDialogPath::class, presentation = Presentation.Modal)
                class ProfileFragment: Fragment(), ViewFragment {
                    override var viewKey: String = ""
                    override lateinit var router: Router
                    override lateinit var localRouter: RouterLocal
                    override lateinit var resultProvider: RouterResultProvider
                }
            """.trimIndent()))

        assertEquals(run.result.messages, KotlinCompilation.ExitCode.OK, run.result.exitCode)
        val profileController = run.generatedFile("test", "ProfileFragment_test_ProfilePath_RouteController.kt").readText()
        val dialogController = run.generatedFile("test", "ProfileFragment_test_ProfileDialogPath_RouteController.kt").readText()
        assertTrue(profileController.contains("arguments.putSerializable(\"userId\", path.userId as Serializable?)"))
        assertTrue(profileController.contains("arguments.putSerializable(\"source\", path.source as Serializable?)"))
        assertTrue(dialogController.contains("class ProfileFragment_test_ProfileDialogPath_RouteController"))
        assertTrue(run.generatedFile("test", "RouterComponentImpl.kt").readText().contains("preferredPresentation = Presentation.Modal"))
    }

    @Test
    fun generatesFragmentFactoryControllerUsingRouterAppDefaultFactory()
    {
        val run = compile(
            SourceFile.kotlin("FragmentFactoryRoute.kt", """
                package test

                import androidx.fragment.app.Fragment
                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.Router
                import com.speakerboxlite.router.RouterLocal
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.annotations.RouterFragment
                import com.speakerboxlite.router.fragment.ViewFragment
                import com.speakerboxlite.router.result.RouterResultProvider

                @RouterApp(viewFactoryName = "build")
                class App

                data class ProfilePath(val userId: String): RoutePath

                @RouterFragment(path = ProfilePath::class)
                class ProfileFragment: Fragment(), ViewFragment {
                    override var viewKey: String = ""
                    override lateinit var router: Router
                    override lateinit var localRouter: RouterLocal
                    override lateinit var resultProvider: RouterResultProvider

                    companion object {
                        fun build(path: ProfilePath): ProfileFragment = ProfileFragment()
                    }
                }
            """.trimIndent()))

        assertEquals(run.result.messages, KotlinCompilation.ExitCode.OK, run.result.exitCode)
        assertTrue(run.generatedFile("test", "ProfileFragment_test_ProfilePath_RouteController.kt").readText()
            .contains("ProfileFragment.build(path)"))
    }

    @Test
    fun reportsFragmentRouteViewModelContractViolation()
    {
        val result = compile(
            SourceFile.kotlin("BadFragmentRoute.kt", """
                package test

                import androidx.fragment.app.Fragment
                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.Router
                import com.speakerboxlite.router.RouterLocal
                import com.speakerboxlite.router.ViewModel
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.annotations.RouterFragment
                import com.speakerboxlite.router.fragment.ViewFragment
                import com.speakerboxlite.router.result.RouterResultProvider

                @RouterApp
                class App

                data class ProfilePath(val userId: String): RoutePath
                class ProfileViewModel: ViewModel

                @RouterFragment(path = ProfilePath::class, viewModel = ProfileViewModel::class)
                class ProfileFragment: Fragment(), ViewFragment {
                    override var viewKey: String = ""
                    override lateinit var router: Router
                    override lateinit var localRouter: RouterLocal
                    override lateinit var resultProvider: RouterResultProvider
                }
            """.trimIndent())).result

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("must implement com.speakerboxlite.router.fragment.ViewFragmentVM"))
    }

    @Test
    fun reportsNonSerializablePathProperties()
    {
        val result = compile(
            SourceFile.kotlin("BadPathRoute.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.controllers.RouteController

                @RouterApp
                class App

                class NotSerializable
                data class BrokenPath(val value: NotSerializable): RoutePath

                class BrokenView: View {
                    override var viewKey: String = ""
                }

                @Route
                abstract class BrokenRouteController: RouteController<BrokenPath, BrokenView>()
            """.trimIndent())).result

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("all path properties must be Serializable"))
        assertTrue(result.messages.contains("value: test.NotSerializable"))
    }

    @Test
    fun reportsNestedNonSerializablePathProperties()
    {
        val result = compile(
            SourceFile.kotlin("BadNestedPathRoute.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.controllers.RouteController
                import java.io.Serializable

                @RouterApp
                class App

                class NotSerializable
                data class SerializableWrapper(val value: NotSerializable): Serializable
                data class BrokenPath(val wrapper: SerializableWrapper): RoutePath

                class BrokenView: View {
                    override var viewKey: String = ""
                }

                @Route
                abstract class BrokenRouteController: RouteController<BrokenPath, BrokenView>()
            """.trimIndent())).result

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("wrapper.value: test.NotSerializable"))
    }

    @Test
    fun acceptsSerializablePathProperties()
    {
        val run = compile(
            SourceFile.kotlin("SerializablePathRoute.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.controllers.RouteController
                import java.io.Serializable

                @RouterApp
                class App

                data class SerializableArg(val value: String): Serializable
                data class SafePath(val arg: SerializableArg): RoutePath

                class SafeView: View {
                    override var viewKey: String = ""
                }

                @Route
                abstract class SafeRouteController: RouteController<SafePath, SafeView>()
            """.trimIndent()))

        assertEquals(run.result.messages, KotlinCompilation.ExitCode.OK, run.result.exitCode)
    }

    private fun compile(vararg sources: SourceFile): CompilationRun
    {
        val workingDir = createTempDirectory("router-ksp-test").toFile()
        val result = KotlinCompilation().apply {
            inheritClassPath = true
            this.workingDir = workingDir
            useKsp2()
            configureKsp {
                symbolProcessorProviders.add(RouterSymbolProcessorProvider())
            }
            this.sources = routerStubs() + sources
            messageOutputStream = System.out
        }.compile()
        return CompilationRun(result, workingDir)
    }

    private data class CompilationRun(
        val result: JvmCompilationResult,
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
            interface RouterLocal
            interface Router {
                fun route(path: RoutePath, presentation: Presentation? = null): Router?
            }
            interface RouterComponent {
                val routeManager: RouteManager
                val resultManager: ResultManager
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
                override fun route(path: RoutePath, presentation: Presentation?): Router? = this
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
            interface RouteControllerComponent<Path: RoutePath, V: View, C: Component> {
                var componentClass: KClass<C>
                fun onInject(component: Any) {}
            }
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

            abstract class RouteControllerVMC<Path: RoutePath, VM: ViewModel, ModelProvider: RouterModelProvider, V: View, C: Component>: RouteController<Path, V>(), RouteControllerComponent<Path, V, C> {
                override lateinit var componentClass: KClass<C>
                protected abstract fun onCreateViewModel(modelProvider: ModelProvider, path: Path): VM
                protected abstract fun onInject(vm: VM, component: C)
            }

            abstract class RouteControllerVM<Path: RoutePath, VM: ViewModel, ModelProvider: RouterModelProvider, V: View>: RouteController<Path, V>() {
                protected abstract fun onCreateViewModel(modelProvider: ModelProvider, path: Path): VM
            }

            abstract class RouteControllerC<Path: RoutePath, V: View, C: Component>: RouteController<Path, V>(), RouteControllerComponent<Path, V, C> {
                override lateinit var componentClass: KClass<C>
                protected abstract fun onInject(view: V, component: C)
            }
        """.trimIndent()),
        SourceFile.kotlin("FragmentStubs.kt", """
            package com.speakerboxlite.router.fragment

            import androidx.fragment.app.Fragment
            import com.speakerboxlite.router.Router
            import com.speakerboxlite.router.RouterLocal
            import com.speakerboxlite.router.View
            import com.speakerboxlite.router.ViewModel
            import com.speakerboxlite.router.controllers.RouterModelProvider
            import com.speakerboxlite.router.result.RouterResultProvider

            interface ViewFragment: View {
                var router: Router
                var localRouter: RouterLocal
                var resultProvider: RouterResultProvider
            }

            interface ViewFragmentVM<VM: ViewModel>: ViewFragment {
                var viewModel: VM
            }

            class FragmentViewModelProvider(val fragment: Fragment): RouterModelProvider
        """.trimIndent()),
        SourceFile.kotlin("AndroidStubs.kt", """
            package android.os

            import java.io.Serializable

            interface Parcelable

            class Bundle {
                fun putSerializable(key: String, value: Serializable?) {}
                fun putParcelable(key: String, value: Parcelable?) {}
            }
        """.trimIndent()),
        SourceFile.kotlin("AndroidXFragmentStubs.kt", """
            package androidx.fragment.app

            import android.os.Bundle

            open class Fragment {
                var arguments: Bundle? = null
            }
        """.trimIndent()),
        SourceFile.kotlin("ResultStubs.kt", """
            package com.speakerboxlite.router.result

            interface ResultManager
            interface RouterResultProvider
            class ResultManagerImpl: ResultManager
        """.trimIndent()),
        SourceFile.kotlin("PatternStubs.kt", """
            package com.speakerboxlite.router.pattern

            class UrlPattern(val value: String)
        """.trimIndent()))
}
