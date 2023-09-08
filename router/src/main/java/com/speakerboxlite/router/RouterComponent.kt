package com.speakerboxlite.router

interface RouterComponent
{
    val startRouter: Router
    val routeManager: RouteManager
    val routerManager: RouterManager
    val resultManager: ResultManager

    fun initialize(component: Any, startPath: RoutePath)
}