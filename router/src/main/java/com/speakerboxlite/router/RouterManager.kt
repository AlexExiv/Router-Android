package com.speakerboxlite.router

interface RouterManager
{
    var top: Router?

    operator fun set(key: String, value: Router?)
    operator fun get(key: String): Router

    fun bind(router: Router, toView: View<*>)
    fun get(forView: View<*>): Router

    fun release(router: Router)
}