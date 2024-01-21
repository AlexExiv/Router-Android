package com.speakerboxlite.router

interface RouterManager
{
    val isAppRestarting: Boolean

    val top: Router

    operator fun set(key: String, value: Router?)
    operator fun get(key: String): Router?

    fun bindView(router: Router, viewKey: String)
    fun unbindView(viewKey: String)
    fun getForView(viewKey: String): Router?

    fun push(viewKey: String, router: Router)

    fun pushReel(viewKey: String, routerTabs: RouterTabs)
    fun switchReel(viewKey: String, index: Int)
    fun remove(viewKey: String)

    fun pop(toKey: String? = null): Router?
}