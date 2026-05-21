package com.speakerboxlite.processor.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
}
