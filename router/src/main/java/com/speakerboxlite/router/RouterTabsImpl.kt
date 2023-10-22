package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import java.util.UUID
import kotlin.reflect.KClass

class RouterTabsImpl(val callerKey: String,
                     val router: RouterSimple,
                     val presentInTab: Boolean): RouterTabs
{
    override var tabChangeCallback: OnTabChangeCallback? = null

    protected val commandBuffer: CommandBuffer = CommandBufferImpl()
    protected val tabRoutes = mutableMapOf<Int, RouterSimple>()
    protected val tabRoutesKeys = mutableMapOf<Int, String>()

    override fun route(index: Int, path: RoutePath, recreate: Boolean): String
    {
        val viewKey = if (tabRoutesKeys[index] == null || recreate)
        {
            val viewKey = UUID.randomUUID().toString()
            val routerTab = router.createRouterTab(callerKey, index, this)
            routerTab.route(path, Presentation.Push)

            tabRoutes[index] = routerTab as RouterSimple
            tabRoutes[index]!!.bindRouter(viewKey)
            tabRoutesKeys[index] = viewKey

            viewKey
        }
        else
            tabRoutesKeys[index]!!

        return viewKey
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