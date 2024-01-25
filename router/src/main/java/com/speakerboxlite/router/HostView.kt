package com.speakerboxlite.router

interface HostCloseable
{
    fun onCloseHost()
}

interface HostView: View
{
    var routerManager: RouterManager
    var router: Router
}
