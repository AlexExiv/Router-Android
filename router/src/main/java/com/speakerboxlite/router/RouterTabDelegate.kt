package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation

interface RouterTabDelegate
{
    val hasPreviousScreen: Boolean

    fun route(path: RoutePath, presentation: Presentation?): String
    fun <R : Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): String

    fun back()

    fun close()
    fun closeTo(key: String)
    fun closeToTop()

    fun tryRouteToTab(path: RoutePath): Router?
}