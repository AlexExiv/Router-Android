package com.speakerboxlite.router

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

    override fun bind(router: Router, toView: View<*>)
    {
        routerByView[toView.viewKey] = router
    }

    override fun get(forView: View<*>): Router = routerByView[forView.viewKey]!!

    override fun release(router: Router)
    {
        TODO("Not yet implemented")
    }
}