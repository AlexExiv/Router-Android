package com.speakerboxlite.router

import android.util.Log

class RouterManagerImpl : RouterManager
{
    override var top: Router? = null

    private val routers = mutableMapOf<String, Router>()
    private val routerByView = mutableMapOf<String, Router>()

    override fun set(key: String, value: Router?)
    {
        if (value == null)
            routers.remove(key)
        else
            routers[key] = value
    }

    override fun get(key: String): Router = routers[key]!!

    override fun bindView(router: Router, viewKey: String)
    {
        routerByView[viewKey] = router
        Log.d("RouterManager", "Bound routers: ${routerByView.size}")
    }

    override fun unbindView(viewKey: String)
    {
        routerByView.remove(viewKey)
        Log.d("RouterManager", "Bound routers after unbind: ${routerByView.size}")
    }

    override fun getForView(viewKey: String): Router = routerByView[viewKey]!!

    override fun release(router: Router)
    {
        val keys = routerByView.filter { it.value == router }.map { it.key }
        for (k in keys)
        {
            routerByView.remove(k)
            routers.remove(k)
        }
        Log.d("RouterManager", "Bound routers after release: ${routerByView.size}")
    }
}