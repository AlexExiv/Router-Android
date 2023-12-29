package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.controllers.RouteParamsGen
import java.lang.ref.WeakReference
import java.util.UUID
import kotlin.reflect.KClass

class RouterTabsImpl(val viewKey: String,
                     val callerKey: String,
                     router: RouterSimple,
                     val presentInTab: Boolean): RouterTabs
{
    protected val weakRouter = WeakReference(router)
    protected val router: RouterSimple get() = weakRouter.get()!!

    override var tabChangeCallback: OnTabChangeCallback? = null

    protected val commandBuffer: CommandBuffer = CommandBufferImpl()
    protected val tabRouters = mutableMapOf<Int, RouterSimple>()
    protected val tabRoutersKeys = mutableMapOf<Int, String>()

    protected var tabIndex = 0

    override fun route(index: Int): Boolean
    {
        if (index == tabIndex)
            return true

        val tabRouter = tabRouters[index]!!
        val route = router.findRoute(tabRouter.rootPath!!)
        val routeParams = RouteParamsGen(path = tabRouter.rootPath!!, presentation = Presentation.Push, tabIndex = index)
        if (router.tryRouteMiddlewares(routeParams, route))
            return false

        showTab(index)
        return true
    }

    override fun route(index: Int, path: RoutePath, recreate: Boolean): String
    {
        val viewKey = if (tabRoutersKeys[index] == null || recreate)
        {
            val viewKey = UUID.randomUUID().toString()
            val routerTab = router.createRouterTab(callerKey, index, this)
            routerTab.route(path, Presentation.Push)

            tabRouters[index] = routerTab as RouterSimple
            tabRouters[index]!!.bindRouter(viewKey)
            tabRoutersKeys[index] = viewKey

            viewKey
        }
        else
            tabRoutersKeys[index]!!

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

    fun closeTabs(): Router?
    {
        releaseRouters()
        return router.close()
    }

    internal fun closeTabsTo(key: String): Boolean
    {
        for (i in tabRouters.keys)
        {
            if (tabRouters[i]!!.containsView(key))
            {
                showTab(i)
                tabRouters[i]!!.closeTo(key)
                return true
            }
        }

        return false
    }

    internal fun closeTabsToTop(): Router?
    {
        releaseRouters()
        return router.closeToTop()
    }

    override operator fun get(index: Int): Router = tabRouters[index]!!

    internal fun scanForPath(clazz: KClass<*>): ViewMeta?
    {
        for (kv in tabRouters)
        {
            val v = kv.value.scanForPath(clazz, false)
            if (v != null)
                return v
        }

        return null
    }

    internal fun releaseRouters()
    {
        //tabRoutes.values.forEach { it.release() }
    }

    internal fun containsPath(clazz: KClass<*>): Int?
    {
        val i = tabRouters.values.indexOfFirst { it.rootPath != null && it.rootPath!!::class == clazz }
        return if (i == -1) null else i
    }

    protected fun showTab(i: Int)
    {
        tabIndex = i
        if (tabChangeCallback != null)
        {
            router.routerManager.switchReel(viewKey, i)
            commandBuffer.apply(Command.ChangeTab(tabChangeCallback!!, i))
        }
    }
}