package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation

class RouterTabsImpl(val callerKey: String,
                     val hostFactory: HostViewFactory,
                     val router: RouterSimple): RouterTabs
{
    override var tabChangeCallback: OnTabChangeCallback? = null

    override fun route(index: Int, path: RoutePath): HostView
    {
        val view = hostFactory.create()
        view.router = router.createRouterTab(callerKey, index, this)
        view.router.route(path, Presentation.Push)
        return view
    }

    fun showFirstTab()
    {
        tabChangeCallback?.invoke(0)
    }

    fun closeTabs()
    {
        router.close()
    }

    fun <R: Any> closeTabsWithResult(result: R)
    {
        router.closeWithResult(result)
    }
}