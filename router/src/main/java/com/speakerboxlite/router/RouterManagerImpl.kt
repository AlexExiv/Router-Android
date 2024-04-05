package com.speakerboxlite.router

import android.util.Log
import com.speakerboxlite.router.annotations.InternalApi

class RouterManagerImpl: RouterManager, RouterStack by RouterStackImpl()
{
    override var isAppRestarting: Boolean = false

    private var rootRouter: RouterSimple? = null

    internal val routers = mutableMapOf<String, Router>()
    internal val routerByView = mutableMapOf<String, Router>()

    override fun set(key: String, value: Router?)
    {
        if (value != null)
        {
            routers[key] = value
            if ((value is RouterSimple) && (value.parent == null))
                rootRouter = value

            Log.d("RouterManager", "Bound routers: ${routers.size}")
        }
        else
        {
            routers.remove(key)
            Log.d("RouterManager", "Bound routers after unbind: ${routers.size}")
        }
    }

    override fun get(key: String): Router? = routers[key]

    @InternalApi
    fun resetToTop()
    {
        isAppRestarting = true
    }
}