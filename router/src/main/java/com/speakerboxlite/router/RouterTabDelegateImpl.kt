package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.RouteType
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
        val _presentation = if (route.routeType.isNoStackStructure) Presentation.Modal else (presentation ?: route.preferredPresentation)

        routerTab?.closeAllNoStack()
        return if ((routerTabs?.tabRouteInParent == true && stackSize > 0) || _presentation == Presentation.Modal || _presentation == Presentation.ModalNewTask)
        {
            if (parent is RouterTab)
                parent?.route(path, presentation)
            else
                parent?.routeInternal(null, path, RouteType.Simple, if (_presentation == Presentation.ModalNewTask) Presentation.ModalNewTask else Presentation.Modal, null)
        }
        else
            routerTab?.routeInternal(routerTab, path, RouteType.Simple, presentation, null)
    }

    override fun <VR: ViewResult, R: Any> routeWithResult(viewResult: VR, path: RoutePathResult<R>, presentation: Presentation?, result: RouterResultDispatcher<VR, R>): Router?
    {
        val route = routerTab?.findRoute(path) ?: return null
        val _presentation = if (route.routeType.isNoStackStructure) Presentation.Modal else (presentation ?: route.preferredPresentation)

        routerTab?.closeAllNoStack()
        val _result = ViewResultData.create(viewResult, result)
        return if ((routerTabs?.tabRouteInParent == true && stackSize > 0) || _presentation == Presentation.Modal || _presentation == Presentation.ModalNewTask)
        {
            if (parent is RouterTab)
                parent?.routeWithResult(viewResult, path, presentation, result)
            else
                parent?.routeInternal(null, path, RouteType.Simple, if (_presentation == Presentation.ModalNewTask) Presentation.ModalNewTask else Presentation.Modal, _result)
        }
        else
            routerTab?.routeInternal(routerTab, path, RouteType.Simple, presentation, _result)
    }

    override fun back(): Router?
    {
        val _routerTabs = routerTabs ?: return null

        return if (stackSize > 1 || parent is RouterTab)
            superRouterTab?.superBack()
        else if (_routerTabs.backToFirst && index != 0)
        {
            check(_routerTabs.tabChangeCallback != null) { "You want to get back to the first tab but haven't specified tabChangeCallback" }
            _routerTabs.showFirstTab()
            _routerTabs.get(0)
        }
        else if (hasPreviousScreen)
            _routerTabs.closeTabs()
        else
            routerTab
    }


    override fun close(): Router? =
        if (stackSize > 1 || parent is RouterTab)
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

    override fun closeTabToTop(): RouterTab?
    {
        (routerTab?.child as? RouterTab)?.closeTabToTop()

        if (routerTab?.parent is RouterTab)
            routerTab?._closeAll()
        else
            routerTab?._closeTo(0)

        return routerTab as RouterTab
    }

    override fun tryRouteToTab(path: RoutePath): Router? = parent?.tryRouteToTab(path)

    override fun createRouterTabs(key: String): RouterTabs = routerTab!!.createRouterTabs(key, true, false)
}