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

    override fun route(path: RoutePath, presentation: Presentation?): String = delegate.route(path, presentation)

    override fun <R : Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): String =
        delegate.routeWithResult(path, presentation, result)

    override fun back()
    {
        delegate.back()
    }

    override fun close()
    {
        delegate.close()
    }

    override fun closeTo(key: String)
    {
        delegate.closeTo(key)
    }

    override fun closeToTop()
    {
        delegate.closeToTop()
    }

    override fun tryRouteToTab(path: RoutePath): Router? = delegate.tryRouteToTab(path)

    override fun superBack()
    {
        super.back()
    }

    override fun superClose()
    {
        super.close()
    }
}
