package com.speakerboxlite.router

import com.speakerboxlite.router.result.ResultManager

interface RouterComponent
{
    val routeManager: RouteManager
    val resultManager: ResultManager
    val routerManager: RouterManager
}