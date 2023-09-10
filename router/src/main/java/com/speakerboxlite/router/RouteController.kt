package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import kotlin.reflect.KClass

interface RouteController<VM: ViewModel>
{
    val singleton: Boolean
    val preferredPresentation: Presentation
    val isChain: Boolean

    fun check(url: String): Boolean
    fun check(path: RoutePath): Boolean
    fun convert(url: String): RoutePath

    fun isPartOfChain(clazz: KClass<*>): Boolean

    fun onCreateView(): View<VM>
    fun <CommonComponent> onComposeView(view: View<*>, path: RoutePath, commonComponent: CommonComponent)
}