package com.speakerboxlite.router

interface RouteManager
{
    fun find(url: String) : RouteController<*>?
    fun find(path: RoutePath) : RouteController<*>?
    fun register(route: RouteController<*>)
}