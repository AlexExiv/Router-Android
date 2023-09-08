package com.speakerboxlite.router

typealias OnTabChangeCallback = (Int) -> Unit

interface RouterTabs
{
    var tabChangeCallback: OnTabChangeCallback?
    fun route(index: Int, path: RoutePath): HostView
}