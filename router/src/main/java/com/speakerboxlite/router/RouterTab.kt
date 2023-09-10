package com.speakerboxlite.router

import com.speakerboxlite.router.result.ResultManager

class RouterTab(callerKey: String?,
                parent: RouterSimple,
                routeManager: RouteManager,
                routerManager: RouterManager,
                resultManager: ResultManager,
                component: Any,
                val index: Int,
                val routerTab: RouterTabsImpl): RouterSimple(callerKey, parent, routeManager, routerManager, resultManager, component)
{
    override fun back()
    {
        if (viewsStack.size > 1)
            super.back()
        else if (routerTab.tabChangeCallback != null && index != 0)
            routerTab.showFirstTab()
        else
            routerTab.closeTabs()
    }

    override fun close()
    {
        if (viewsStack.size > 1)
            super.close()
        else
            routerTab.closeTabs()
    }

    override fun closeTo(key: String)
    {
        val i = viewsStack.indexOfFirst { it.key == key }
        if (i != -1)
        {
            _closeTo(i)
        }
        else
        {
            parent!!.closeTo(key)
            routerTab.closeTabs()
        }
    }

    override fun closeToTop()
    {
        if (parent!!.isCurrentTop)
        {
            _closeTo(0)
        }
        else
        {
            routerTab.closeTabs()
            parent.closeToTop()
        }
    }
}