package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation

class RouterTabsImpl(val callerKey: String,
                     val hostFactory: HostViewFactory,
                     val router: RouterSimple): RouterTabs
{
    override var tabChangeCallback: OnTabChangeCallback? = null

    protected val commandBuffer: CommandBuffer = CommandBufferImpl()

    override fun route(index: Int, path: RoutePath): HostView
    {
        val view = hostFactory.create()
        view.router = router.createRouterTab(callerKey, index, this)
        view.router.route(path, Presentation.Push)
        return view
    }

    override fun bindExecutor(executor: CommandExecutor)
    {
        commandBuffer.bind(executor)
    }

    override fun unbindExecutor()
    {
        commandBuffer.unbind()
    }

    fun showFirstTab()
    {
        if (tabChangeCallback != null)
            commandBuffer.apply(Command.ChangeTab(tabChangeCallback!!, 0))
    }

    fun closeTabs()
    {
        router.close()
    }
}