package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.result.ResultManager

class RouterTabInjector(callerKey: String?,
                        parent: RouterSimple,
                        routeManager: RouteManager,
                        routerManager: RouterManager,
                        resultManager: ResultManager,
                        componentProvider: ComponentProvider,
                        val index: Int,
                        val routerTabs: RouterTabsImpl): RouterInjector(callerKey, parent, routeManager, routerManager, resultManager, componentProvider)
{
    override val hasPreviousScreen: Boolean get() = viewsStack.size > 1 || parent!!.hasPreviousScreen

    override fun route(path: RoutePath, presentation: Presentation?): String =
        if (routerTabs.presentInTab && viewsStack.isNotEmpty())
            super.route(path, Presentation.Modal)
        else
            super.route(path, presentation)

    override fun <R : Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): String =
        if (routerTabs.presentInTab && viewsStack.isNotEmpty())
            super.routeWithResult(path, Presentation.Modal, result)
        else
            super.routeWithResult(path, presentation, result)

    override fun back()
    {
        if (viewsStack.size > 1)
            super.back()
        else if (routerTabs.tabChangeCallback != null && index != 0)
            routerTabs.showFirstTab()
        else if (hasPreviousScreen)
            routerTabs.closeTabs()
    }

    override fun close()
    {
        if (viewsStack.size > 1)
            super.close()
        else
            routerTabs.closeTabs()
    }

    override fun closeTo(key: String)
    {
        val i = viewsStack.indexOfFirst { it.key == key }
        if (i != -1)
        {
            _closeTo(i)
        }
        else if (!routerTabs.closeTabsTo(key))
        {
            parent?.closeTo(key)
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
            routerTabs.closeTabsToTop()
        }
    }
/*
    override fun scanForPath(clazz: KClass<*>, recursive: Boolean): ViewMeta?
    {
        if (recursive)
        {
            val v = routerTabs.scanForPath(clazz)
            if (v != null)
                return v

            if (parent != null)
                return parent.scanForPath(clazz)

            return null
        }

        return viewsStack.lastOrNull { it.path == clazz }
    }*/
}
