package com.speakerboxlite.processor.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class SerializablePathValidationTest
{
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

    @Test
    fun acceptsCustomSerializableContractPathProperties()
    {
        val run = compile(
            SourceFile.kotlin("CustomSerializablePathRoute.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.controllers.RouteController

                interface Serializable

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

    @Test
    fun acceptsSerializableArrayListPathProperties()
    {
        val run = compile(
            SourceFile.kotlin("SerializableArrayListPathRoute.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.controllers.RouteController

                interface Serializable

                @RouterApp
                class App

                data class Color(
                    var r: Float = 0f,
                    var g: Float = 0f,
                    var b: Float = 0f,
                    var a: Float = 1f): Serializable

                data class ColorPickerPath(
                    val color: Color,
                    val predefinedColors: ArrayList<Color> = ArrayList(),
                    val transparency: Boolean = false): RoutePath

                class ColorPickerView: View {
                    override var viewKey: String = ""
                }

                @Route
                abstract class ColorPickerRouteController: RouteController<ColorPickerPath, ColorPickerView>()
            """.trimIndent()))

        assertEquals(run.result.messages, KotlinCompilation.ExitCode.OK, run.result.exitCode)
    }

    @Test
    fun acceptsSerializableCollectionPathProperties()
    {
        val run = compile(
            SourceFile.kotlin("SerializableCollectionPathRoute.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.controllers.RouteController

                interface Serializable

                @RouterApp
                class App

                data class Color(
                    var r: Float = 0f,
                    var g: Float = 0f,
                    var b: Float = 0f,
                    var a: Float = 1f): Serializable

                data class ColorPickerPath(
                    val colors: List<Color>,
                    val selected: Set<Color>,
                    val named: Map<String, Color>): RoutePath

                class ColorPickerView: View {
                    override var viewKey: String = ""
                }

                @Route
                abstract class ColorPickerRouteController: RouteController<ColorPickerPath, ColorPickerView>()
            """.trimIndent()))

        assertEquals(run.result.messages, KotlinCompilation.ExitCode.OK, run.result.exitCode)
    }

    @Test
    fun reportsNonSerializableArrayListPathProperties()
    {
        val result = compile(
            SourceFile.kotlin("NonSerializableArrayListPathRoute.kt", """
                package test

                import com.speakerboxlite.router.RoutePath
                import com.speakerboxlite.router.View
                import com.speakerboxlite.router.annotations.Route
                import com.speakerboxlite.router.annotations.RouterApp
                import com.speakerboxlite.router.controllers.RouteController

                @RouterApp
                class App

                class Color
                data class ColorPickerPath(val predefinedColors: ArrayList<Color> = ArrayList()): RoutePath

                class ColorPickerView: View {
                    override var viewKey: String = ""
                }

                @Route
                abstract class ColorPickerRouteController: RouteController<ColorPickerPath, ColorPickerView>()
            """.trimIndent())).result

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("predefinedColors<0>: test.Color"))
    }
}
