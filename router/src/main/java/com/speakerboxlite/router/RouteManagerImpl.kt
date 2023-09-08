package com.speakerboxlite.router

class RouteManagerImpl: RouteManager
{
    val routes = mutableListOf<RouteController<*>>()

    override fun find(url: String): RouteController<*>? = routes.firstOrNull { it.check(url) }

    override fun find(path: RoutePath): RouteController<*>? = routes.firstOrNull { it.check(path) }

    override fun register(route: RouteController<*>)
    {
        routes.add(route)
    }
}