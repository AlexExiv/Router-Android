package com.speakerboxlite.router

import com.speakerboxlite.router.result.ResultManager
import kotlin.reflect.KClass

class RouterTabInjector(callerKey: String?,
                        parent: RouterSimple,
                        routeManager: RouteManager,
                        routerManager: RouterManager,
                        resultManager: ResultManager,
                        componentProvider: ComponentProvider,
                        val index: Int,
                        val routerTab: RouterTabsImpl): RouterInjector(callerKey, parent, routeManager, routerManager, resultManager, componentProvider)
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
            if (routerTab.closeTo(key))
            {

            }
            else
            {
                parent!!.closeTo(key)
                routerTab.closeTabs()
            }
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

    override fun scanForPath(clazz: KClass<*>, recursive: Boolean): ViewMeta?
    {
        if (recursive)
        {
            val v = routerTab.scanForPath(clazz)
            if (v != null)
                return v

            if (parent != null)
                return parent.scanForPath(clazz)

            return null
        }

        return viewsStack.lastOrNull { it.path == clazz }
    }
}
