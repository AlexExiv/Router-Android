package com.speakerboxlite.router

import com.speakerboxlite.router.result.ResultManager

interface RouterComponent
{
    val startRouter: Router
    val routeManager: RouteManager
    val routerManager: RouterManager
    val componentProvider: ComponentProvider
    val resultManager: ResultManager

    fun initialize(component: Any, startPath: RoutePath)
}