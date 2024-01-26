package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.RouteType
import com.speakerboxlite.router.result.ResultManager
import java.lang.ref.WeakReference

class RouterTabInjector(callerKey: String?,
                        parent: RouterSimple,
                        routeManager: RouteManager,
                        routerManager: RouterManager,
                        resultManager: ResultManager,
                        componentProvider: ComponentProvider,
                        override val index: Int,
                        routerTabs: RouterTabsImpl): RouterInjector(callerKey, parent, routeManager, routerManager, resultManager, componentProvider), RouterTabSuper, RouterTab
{
    private val weakRouterTabs = WeakReference(routerTabs)

    private val delegate: RouterTabDelegate = RouterTabDelegateImpl(index, routerTabs, this, this, parent)

    override val hasPreviousScreen: Boolean get() = delegate.hasPreviousScreen

    override fun route(path: RoutePath, presentation: Presentation?): Router? = delegate.route(path, presentation)

    override fun <VR: ViewResult, R: Any> routeWithResult(viewResult: VR, path: RoutePathResult<R>, presentation: Presentation?, result: RouterResultDispatcher<VR, R>): Router? =
        delegate.routeWithResult(viewResult, path, presentation, result)

    override fun back(): Router? = delegate.back()

    override fun close(): Router? = delegate.close()

    override fun closeTo(key: String): Router? = delegate.closeTo(key)

    override fun closeToTop(): Router? = delegate.closeToTop()

    override fun tryRouteToTab(path: RoutePath): Router? = delegate.tryRouteToTab(path)

    override fun superBack(): Router? = super.back()

    override fun superClose(): Router? = super.close()

    override fun createRouter(callerKey: String): Router = RouterTabInjector(callerKey, this, routeManager, routerManager, resultManager, componentProvider, index, weakRouterTabs.get()!!)

    override fun closeTabToTop(): RouterTab? = delegate.closeTabToTop()
}
