package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.result.RouterResultProvider
import kotlin.reflect.KClass

interface RouteController<Path: RoutePath, V: View>
{
    val singleton: Boolean
    val creatingInjector: Boolean
    val preferredPresentation: Presentation
    val isChain: Boolean

    fun check(url: String): Boolean
    fun check(path: RoutePath): Boolean
    fun convert(url: String): RoutePath

    fun isPartOfChain(clazz: KClass<*>): Boolean

    fun onCreateView(path: Path): V
}

interface RouteControllerComposable<Path: RoutePath, V: View>
{
    fun onComposeView(router: Router, view: V, path: Path)
}

interface RouteControllerComponent<Path: RoutePath, V: View>
{
    fun onCreateInjector(path: Path, component: Any): Any
    fun onComposeView(router: Router, view: V, path: Path, component: Any)
}