package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.result.ResultManager
import java.lang.ref.WeakReference

class RouterTabSimple(callerKey: String?,
                      parent: RouterSimple,
                      routeManager: RouteManager,
                      routerManager: RouterManager,
                      resultManager: ResultManager,
                      val index: Int,
                      routerTabs: RouterTabsImpl): RouterSimple(callerKey, parent, routeManager, routerManager, resultManager), RouterTabSuper, RouterTab
{
    private val weakRouterTabs = WeakReference(routerTabs)

    private val delegate: RouterTabDelegate = RouterTabDelegateImpl(index, routerTabs, this, this, parent)

    override val hasPreviousScreen: Boolean get() = delegate.hasPreviousScreen

    override fun route(path: RoutePath, presentation: Presentation?): Router? = delegate.route(path, presentation)

    override fun <VR: ViewResult, R: Any> routeWithResult(viewResult: VR, path: RoutePathResult<R>, presentation: Presentation?, result: RouterResultDispatcher<VR, R>): Router? =
        delegate.routeWithResult(viewResult, path, presentation, result)

    override fun back() = delegate.back()

    override fun close() = delegate.close()

    override fun closeTo(key: String) = delegate.closeTo(key)

    override fun closeToTop() = delegate.closeToTop()

    override fun tryRouteToTab(path: RoutePath): Router? = delegate.tryRouteToTab(path)

    override fun superBack() = super.back()

    override fun superClose() = super.close()

    override fun createRouter(callerKey: String): Router = RouterTabSimple(callerKey, this, routeManager, routerManager, resultManager, index, weakRouterTabs.get()!!)
}