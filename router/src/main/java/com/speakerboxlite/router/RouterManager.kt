package com.speakerboxlite.router

interface RouterManager
{
    val isAppRestarting: Boolean

    var top: Router?

    operator fun set(key: String, value: Router?)
    operator fun get(key: String): Router?

    fun bindView(router: Router, viewKey: String)
    fun unbindView(viewKey: String)
    fun getForView(viewKey: String): Router?
}