package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import kotlin.reflect.KClass

interface RouteController<Path: RoutePath, VM: ViewModel>
{
    val singleton: Boolean
    val creatingInjector: Boolean
    val preferredPresentation: Presentation
    val isChain: Boolean

    fun check(url: String): Boolean
    fun check(path: RoutePath): Boolean
    fun convert(url: String): RoutePath

    fun isPartOfChain(clazz: KClass<*>): Boolean

    fun onCreateView(): View<VM>
    fun onCreateInjector(path: Path, component: Any): Any
    fun onComposeView(view: View<*>, path: RoutePath, component: Any)
}