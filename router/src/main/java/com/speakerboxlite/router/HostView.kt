package com.speakerboxlite.router

interface HostCloseable
{
    fun onCloseHost()
}

interface HostView: View
{
    var router: Router
}
