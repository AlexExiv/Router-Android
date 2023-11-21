package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation

interface RouterTabDelegate
{
    val hasPreviousScreen: Boolean

    fun route(path: RoutePath, presentation: Presentation?): Router?
    fun <R : Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): Router?

    fun back(): Router?

    fun close(): Router?
    fun closeTo(key: String): Router?
    fun closeToTop(): Router?

    fun tryRouteToTab(path: RoutePath): Router?
}