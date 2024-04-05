package com.speakerboxlite.router

import java.lang.ref.WeakReference

class RouterTabLocal(viewKey: String,
                     router: RouterSimple,
                     override val index: Int,
                     routerTabs: RouterTabsLocal,
                     val rootPath: RoutePath): RouterLocalImpl(viewKey, router), RouterTab
{
    private val weakRouterTabs = WeakReference(routerTabs)
    override val routerTabs: RouterTabs get() = weakRouterTabs.get()!!

    init
    {
        routeInternal(rootPath)
    }

    override fun closeTabToTop(): RouterTab? = this
}