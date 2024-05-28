package com.speakerboxlite.router

import android.os.Bundle
import android.util.Log
import com.speakerboxlite.router.annotations.InternalApi

data class RouterRecord(
    val router: Router,
    var count: Int = 0)

class RouterManagerImpl: RouterManager, RouterStack by RouterStackImpl()
{
    override var isAppRestarting: Boolean = false

    private var isRestored = false
    private var rootRouter: RouterSimple? = null

    internal val routers = mutableMapOf<String, RouterRecord>()
    internal val routerByView = mutableMapOf<String, Router>()

    override fun push(router: Router)
    {
        if (routers[router.key] == null)
            routers[router.key] = RouterRecord(router)
    }

    override fun set(key: String, value: Router?)
    {
        if (value != null)
        {
            push(value)

            routers[value.key]!!.count += 1
            routerByView[key] = value

            if ((value is RouterSimple) && (value.parent == null))
                rootRouter = value

            Log.d("RouterManager", "Bound routers: ${routerByView.size}")
        }
        else
        {
            val router = routerByView.remove(key)
            if (router != null)
            {
                routers[router.key]!!.count -= 1
                if (routers[router.key]!!.count == 0)
                    routers.remove(router.key)
            }

            Log.d("RouterManager", "Bound routers after unbind: ${routerByView.size}")
        }
    }

    override fun get(key: String): Router? = routerByView[key]

    override fun performSave(bundle: Bundle)
    {
        val root = Bundle()

        if (rootRouter != null)
        {
            val routerBundle = Bundle()
            rootRouter!!.performSave(routerBundle)
            root.putBundle(ROOT_ROUTER, routerBundle)
        }

        val routerByViewBundle = Bundle()
        routerByView.forEach {
            routerByViewBundle.putString(it.key, it.value.key)
        }
        root.putBundle(ROUTER_BY_VIEW, routerByViewBundle)

        performSaveStack(root)
        bundle.putBundle(ROOT, root)
    }

    override fun performRestore(bundle: Bundle?)
    {
        //if (isRestored) // restore state only once in case when the App has been recreated because the Router lives in the App's context
            //return

        isRestored = true
        val bundle = bundle ?: return

        val root = bundle.getBundle(ROOT)!!

        routers.clear()
        val routerBundle = root.getBundle(ROOT_ROUTER)
        if (routerBundle != null)
        {
            rootRouter!!.performRestore(routerBundle)
        }

        routerByView.clear()
        val routerByViewBundle = root.getBundle(ROUTER_BY_VIEW)!!
        routerByViewBundle.keySet().forEach {
            this[it] = routers[routerByViewBundle.getString(it)!!]!!.router
        }

        performRestore(root, this)
    }

    @InternalApi
    fun resetToTop()
    {
        isAppRestarting = true
    }

    companion object
    {
        val ROOT = "com.speakerboxlite.router.RouterManagerImpl"
        val ROOT_ROUTER = "com.speakerboxlite.router.RouterManagerImpl.rootRouter"
        val ROUTER_BY_VIEW = "com.speakerboxlite.router.RouterManagerImpl.routerByView"
    }
}