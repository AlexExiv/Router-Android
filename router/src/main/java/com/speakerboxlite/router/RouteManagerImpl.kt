package com.speakerboxlite.router

import com.speakerboxlite.router.controllers.RouteControllerInterface

class RouteManagerImpl: RouteManager
{
    val routes = mutableListOf<RouteControllerInterface<RoutePath, *>>()

    override fun find(url: String): RouteControllerInterface<RoutePath, *>? = routes.firstOrNull { it.check(url) }

    override fun find(path: RoutePath): RouteControllerInterface<RoutePath, *>? = routes.firstOrNull { it.check(path) }

    override fun <Path: RoutePath> register(route: RouteControllerInterface<Path, *>)
    {
        routes.add(route as RouteControllerInterface<RoutePath, *>)
    }
}