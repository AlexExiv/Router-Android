package com.speakerboxlite.router

interface HostCloseable
{
    fun closeHost()
}

interface HostView: View
{
    var router: Router
}
