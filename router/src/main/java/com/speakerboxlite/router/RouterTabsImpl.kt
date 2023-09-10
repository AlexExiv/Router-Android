package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import kotlin.reflect.KClass

class RouterTabsImpl(val callerKey: String,
                     val hostFactory: HostViewFactory,
                     val router: RouterSimple): RouterTabs
{
    override var tabChangeCallback: OnTabChangeCallback? = null

    protected val commandBuffer: CommandBuffer = CommandBufferImpl()
    protected val tabRoutes = mutableMapOf<Int, RouterTab>()

    override fun route(index: Int, path: RoutePath): HostView
    {
        val view = hostFactory.create()
        view.router = router.createRouterTab(callerKey, index, this)
        view.router.route(path, Presentation.Push)
        tabRoutes[index] = view.router as RouterTab

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
        router.close()
    }

    fun closeTo(key: String): Boolean
    {
        for (i in tabRoutes.keys)
        {
            if (tabRoutes[i]!!.containsView(key))
            {
                showTab(i)
                tabRoutes[i]!!.closeTo(key)
                return true
            }
        }

        return false
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

    protected fun showTab(i: Int)
    {
        if (tabChangeCallback != null)
            commandBuffer.apply(Command.ChangeTab(tabChangeCallback!!, i))
    }
}