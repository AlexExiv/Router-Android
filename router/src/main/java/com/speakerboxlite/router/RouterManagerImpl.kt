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
        //if (value == null)
            //routers.remove(key)
        //else

        if (value != null)
        {
            routers[key] = value
            if ((value is RouterSimple) && (value.parent == null))
                rootRouter = value
        }
    }

    override fun get(key: String): Router? = routers[key]

    override fun bindView(router: Router, viewKey: String)
    {
        routerByView[viewKey] = router
        Log.d("RouterManager", "Bound routers: ${routerByView.size}")
    }

    override fun unbindView(viewKey: String)
    {
        //routerByView.remove(viewKey)
        Log.d("RouterManager", "Bound routers after unbind: ${routerByView.size}")
    }

    override fun getForView(viewKey: String): Router? = routerByView[viewKey]

    @InternalApi
    fun resetToTop()
    {
        isAppRestarting = true
    }
}