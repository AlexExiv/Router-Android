package com.speakerboxlite.router

import android.util.Log

class RouterManagerImpl : RouterManager
{
    override var top: Router? = null

    val routers = mutableMapOf<String, Router>()
    val routerByView = mutableMapOf<String, Router>()

    override fun set(key: String, value: Router?)
    {
        if (value == null)
            routers.remove(key)
        else
            routers[key] = value
    }

    override fun get(key: String): Router = routers[key]!!

    override fun bind(router: Router, toView: View)
    {
        routerByView[toView.viewKey] = router
        Log.d("RouterManager", "Bound routers: ${routerByView.size}")
    }

    override fun unbindView(viewKey: String)
    {
        routerByView.remove(viewKey)
        Log.d("RouterManager", "Bound routers after unbind: ${routerByView.size}")
    }

    override fun get(forView: View): Router = routerByView[forView.viewKey]!!

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