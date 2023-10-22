package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import kotlin.reflect.KClass

class RouterTabsImpl(val callerKey: String,
                     val hostFactory: HostViewFactory,
                     val router: RouterSimple,
                     val presentInTab: Boolean): RouterTabs
{
    override var tabChangeCallback: OnTabChangeCallback? = null

    protected val commandBuffer: CommandBuffer = CommandBufferImpl()
    protected val tabRoutes = mutableMapOf<Int, RouterSimple>()

    override fun route(index: Int, path: RoutePath): HostView
    {
        val view = hostFactory.create()
        view.router = router.createRouterTab(callerKey, index, this)
        view.router.route(path, Presentation.Push)
        tabRoutes[index] = view.router as RouterSimple

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
        showTab(0)
    }

    fun closeTabs()
    {
        releaseRouters()
        router.close()
    }

    fun closeTabsTo(key: String)
    {
        for (i in tabRoutes.keys)
        {
            if (tabRoutes[i]!!.containsView(key))
            {
                showTab(i)
                tabRoutes[i]!!.closeTo(key)
                return
            }
        }

        releaseRouters()
        router.closeTo(key)
    }

    fun closeTabsToTop()
    {
        releaseRouters()
        router.closeToTop()
    }

    fun scanForPath(clazz: KClass<*>): ViewMeta?
    {
        for (kv in tabRoutes)
        {
            val v = kv.value.scanForPath(clazz, false)
            if (v != null)
                return v
        }

        return null
    }

    internal fun releaseRouters()
    {
        //tabRoutes.values.forEach { it.releaseRouter() }
    }

    protected fun showTab(i: Int)
    {
        if (tabChangeCallback != null)
            commandBuffer.apply(Command.ChangeTab(tabChangeCallback!!, i))
    }
}