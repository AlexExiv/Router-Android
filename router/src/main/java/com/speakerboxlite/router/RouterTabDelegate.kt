package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation

interface RouterTabDelegate
{
    val hasPreviousScreen: Boolean

    fun route(path: RoutePath, presentation: Presentation?): Router?
    fun <VR: ViewResult, R: Any> routeWithResult(viewResult: VR, path: RoutePathResult<R>, presentation: Presentation?, result: RouterResultDispatcher<VR, R>): Router?

    fun back(): Router?

    fun close(): Router?
    fun closeTo(key: String): Router?
    fun closeToTop(): Router?
    fun closeTabToTop(): RouterTab?

    fun tryRouteToTab(path: RoutePath): Router?
}