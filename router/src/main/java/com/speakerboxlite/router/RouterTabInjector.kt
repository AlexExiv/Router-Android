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
                        routerTabs: RouterTabsImpl): RouterInjector(callerKey, parent, routeManager, routerManager, resultManager, componentProvider), RouterTabSuper
{
    private val delegate: RouterTabDelegate = RouterTabDelegateImpl(index, routerTabs, this, this, parent)

    override val hasPreviousScreen: Boolean get() = delegate.hasPreviousScreen

    override fun route(path: RoutePath, presentation: Presentation?): Router? = delegate.route(path, presentation)

    override fun <R : Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): Router? =
        delegate.routeWithResult(path, presentation, result)

    override fun back(): Router? = delegate.back()

    override fun close(): Router? = delegate.close()

    override fun closeTo(key: String): Router? = delegate.closeTo(key)

    override fun closeToTop(): Router? = delegate.closeToTop()

    override fun tryRouteToTab(path: RoutePath): Router? = delegate.tryRouteToTab(path)

    override fun superBack(): Router? = super.back()

    override fun superClose(): Router? = super.close()
}
