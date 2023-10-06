package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.View
import com.speakerboxlite.router.annotations.Presentation
import java.io.Serializable
import kotlin.reflect.KClass

interface RouteControllerInterface<Path: RoutePath, V: View>
{
    val singleton: Boolean
    val creatingInjector: Boolean
    val preferredPresentation: Presentation
    val isChain: Boolean

    val params: Serializable?

    fun check(url: String): Boolean
    fun check(path: RoutePath): Boolean
    fun convert(url: String): RoutePath

    fun isPartOfChain(clazz: KClass<*>): Boolean

    fun animationController(): AnimationController<RoutePath, View>?
    fun onCreateView(path: Path): V
}

interface RouteControllerComposable<Path: RoutePath, V: View>
{
    fun onComposeView(router: Router, view: V, path: Path)
}

interface Component

interface RouteControllerComponent<Path: RoutePath, V: View, C: Component>
{
    fun onCreateInjector(path: Path, component: Any): Any
    fun onComposeView(router: Router, view: V, path: Path, component: Any)
}