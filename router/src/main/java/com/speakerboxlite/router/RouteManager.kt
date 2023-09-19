package com.speakerboxlite.router

import com.speakerboxlite.router.controllers.RouteControllerInterface

interface RouteManager
{
    fun find(url: String) : RouteControllerInterface<RoutePath, *>?
    fun find(path: RoutePath) : RouteControllerInterface<RoutePath, *>?
    fun <Path: RoutePath> register(route: RouteControllerInterface<Path, *>)
}