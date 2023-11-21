package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import java.lang.ref.WeakReference

class RouterTabDelegateImpl(val index: Int,
                            routerTabs: RouterTabsImpl,
                            routerTab: RouterSimple,
                            superRouterTab: RouterTabSuper,
                            parent: RouterSimple): RouterTabDelegate
{
    private val weakRouterTabs = WeakReference(routerTabs)
    private val weakRouterTab = WeakReference(routerTab)
    private val weakSuperRouterTab = WeakReference(superRouterTab)
    private val weakParent = WeakReference(parent)

    private val routerTabs: RouterTabsImpl? get() = weakRouterTabs.get()
    private val routerTab: RouterSimple? get() = weakRouterTab.get()
    private val superRouterTab: RouterTabSuper? get() = weakSuperRouterTab.get()
    private val parent: RouterSimple? get() = weakParent.get()

    private val stackSize: Int get() = routerTab?.viewsStack?.size ?: 0

    override val hasPreviousScreen: Boolean get() = stackSize > 1 || parent!!.hasPreviousScreen

    override fun route(path: RoutePath, presentation: Presentation?): Router?
    {
        val route = routerTab?.findRoute(path) ?: return null
        val _presentation = presentation ?: route.preferredPresentation

        return if ((routerTabs?.presentInTab == true && stackSize > 0) || route.isTabs || _presentation == Presentation.Modal)
            parent?.route(null, path, RouteType.Simple, Presentation.Modal, routerTab?.viewsStack?.lastOrNull()?.key, null)
        else
            routerTab?.route(routerTab, path, RouteType.Simple, presentation, null, null)
    }

    override fun <R : Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): Router?
    {
        val route = routerTab?.findRoute(path) ?: return null
        return if ((routerTabs?.presentInTab == true && stackSize > 0) || route.isTabs || presentation == Presentation.Modal)
            parent?.route(null, path, RouteType.Simple, Presentation.Modal, routerTab?.viewsStack?.lastOrNull()?.key) { result(it as R) }
        else
            routerTab?.route(routerTab, path, RouteType.Simple, presentation, null) { result(it as  R) }
    }

    override fun back(): Router? =
        if (stackSize > 1)
            superRouterTab?.superBack()
        else if (routerTabs?.tabChangeCallback != null && index != 0)
        {
            routerTabs?.showFirstTab()
            routerTabs?.get(0)
        }
        else if (hasPreviousScreen)
            routerTabs?.closeTabs()
        else
            routerTab

    override fun close(): Router? =
        if (stackSize > 1)
            superRouterTab?.superClose()
        else
            routerTabs?.closeTabs()

    override fun closeTo(key: String): Router?
    {
        val i = routerTab?.viewsStack?.indexOfFirst { it.key == key }
        return if (i != null && i != -1)
            routerTab?._closeTo(i)
        else if (routerTabs?.closeTabsTo(key) == false)
            parent?.closeTo(key)
        else
            routerTab
    }

    override fun closeToTop(): Router? =
        if (parent!!.isCurrentTop)
            routerTab?._closeTo(0)
        else
            routerTabs?.closeTabsToTop()

    override fun tryRouteToTab(path: RoutePath): Router? = parent?.tryRouteToTab(path)
}