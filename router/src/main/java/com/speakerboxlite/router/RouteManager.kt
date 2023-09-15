package com.speakerboxlite.router

interface RouteManager
{
    fun find(url: String) : RouteController<RoutePath, *>?
    fun find(path: RoutePath) : RouteController<RoutePath, *>?
    fun <Path: RoutePath> register(route: RouteController<Path, *>)
}