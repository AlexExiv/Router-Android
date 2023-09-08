package com.speakerboxlite.router

interface RouterLocal: Router
{
    fun routeInContainer(containerId: Int, path: RoutePath)
}