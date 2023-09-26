package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
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
    override fun route(path: RoutePath, presentation: Presentation?): String =
        if (routerTab.presentInTab && viewsStack.isNotEmpty())
            super.route(path, Presentation.Modal)
        else
            super.route(path, presentation)

    override fun <R : Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): String =
        if (routerTab.presentInTab && viewsStack.isNotEmpty())
            super.routeWithResult(path, Presentation.Modal, result)
        else
            super.routeWithResult(path, presentation, result)

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
            routerTab.closeTabsTo(key)
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
            routerTab.closeTabsToTop()
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
