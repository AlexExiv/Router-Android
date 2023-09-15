package com.speakerboxlite.router

class RouteManagerImpl: RouteManager
{
    val routes = mutableListOf<RouteController<RoutePath, *>>()

    override fun find(url: String): RouteController<RoutePath, *>? = routes.firstOrNull { it.check(url) }

    override fun find(path: RoutePath): RouteController<RoutePath, *>? = routes.firstOrNull { it.check(path) }

    override fun <Path: RoutePath> register(route: RouteController<Path, *>)
    {
        routes.add(route as RouteController<RoutePath, *>)
    }
}