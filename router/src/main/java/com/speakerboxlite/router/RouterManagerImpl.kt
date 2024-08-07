package com.speakerboxlite.router

import android.os.Bundle
import com.speakerboxlite.router.annotations.InternalApi

data class RouterRecord(
    val router: Router,
    var count: Int = 0)

class RouterManagerImpl: RouterManager, RouterStack by RouterStackImpl()
{
    override var isAppRestarting: Boolean = false

    private var isRestored = false
    private var rootRouter: RouterSimple? = null

    private val routers = mutableMapOf<String, RouterRecord>()
    internal val routerByView = mutableMapOf<String, Router>()

    private val dataStorage = PathDataStorageImpl()

    override fun push(router: Router)
    {
        if (routers[router.key] == null)
            routers[router.key] = RouterRecord(router)
    }

    override fun getByKey(key: String): Router? = routers[key]?.router

    override fun set(key: String, value: Router?)
    {
        if (value != null)
        {
            push(value)

            routers[value.key]!!.count += 1
            routerByView[key] = value

            if ((value is RouterSimple) && (value.parent == null))
                rootRouter = value

            RouterConfigGlobal.log(TAG, "Bound routers: ${routerByView.size}")
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

            RouterConfigGlobal.log(TAG, "Bound routers after unbind: ${routerByView.size}")
        }
    }

    override fun get(key: String): Router? = routerByView[key]

    override fun performSave(bundle: Bundle)
    {
        val root = Bundle()

        dataStorage.performSave(root)

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
        if (isRestored && RouterConfigGlobal.restoreSingleTime) // restore state only once in case when the App has been recreated because the Router lives in the App's context
            return

        isRestored = true
        bundle ?: return

        val root = bundle.getBundle(ROOT)!!

        dataStorage.performRestore(root)

        routers.clear()
        val routerBundle = root.getBundle(ROOT_ROUTER)
        if (routerBundle != null)
        {
            rootRouter!!.performRestore(routerBundle)
        }

        routerByView.clear()
        val routerByViewBundle = root.getBundle(ROUTER_BY_VIEW)!!
        routerByViewBundle.keySet().forEach { k ->
            if (routers[routerByViewBundle.getString(k)!!] == null)
                RouterConfigGlobal.log(TAG, "Unexpected View Key RouterManager")
            routers[routerByViewBundle.getString(k)!!]?.also { this[k] = it.router } // weird bug
            //this[it] = routers[routerByViewBundle.getString(it)!!]!!.router
        }

        performRestore(root, this)
    }

    override fun provideDataStorage(): PathDataStorage = dataStorage

    @InternalApi
    fun resetToTop()
    {
        isAppRestarting = true
    }

    companion object
    {
        val TAG = "RouterManagerImpl"

        val ROOT = "com.speakerboxlite.router.RouterManagerImpl"
        val ROOT_ROUTER = "com.speakerboxlite.router.RouterManagerImpl.rootRouter"
        val ROUTER_BY_VIEW = "com.speakerboxlite.router.RouterManagerImpl.routerByView"
    }
}