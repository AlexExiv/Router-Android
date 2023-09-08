package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation

interface RouteController<VM: ViewModel>
{
    val singleTop: Boolean
    val preferredPresentation: Presentation

    fun check(url: String): Boolean
    fun check(path: RoutePath): Boolean
    fun convert(url: String): RoutePath

    fun onCreateView(): View<VM>
    fun <CommonComponent> onComposeView(view: View<*>, path: RoutePath, commonComponent: CommonComponent)
}