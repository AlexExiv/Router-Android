package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.command.ViewFactory
import com.speakerboxlite.router.controllers.RouteParamsGen
import java.lang.ref.WeakReference
import java.util.UUID

class RouterTabsLocal(
    val viewKey: String,
    router: RouterSimple): RouterTabs
{
    override var tabChangeCallback: OnTabChangeCallback? = null
    override var tabIndex: Int = 0

    protected val weakRouter = WeakReference(router)
    protected val router: RouterSimple get() = weakRouter.get()!!

    protected val commandBuffer: CommandBuffer = CommandBufferImpl(null)
    protected val tabRouters = mutableMapOf<Int, RouterTabLocal>()
    protected val tabRoutersKeys = mutableMapOf<Int, String>()

    override fun route(index: Int): Boolean
    {
        if (index == tabIndex)
            return true

        val tabRouter = tabRouters[index]!!
        val route = router.findRoute(tabRouter.rootPath)
        val routeParams = RouteParamsGen(path = tabRouter.rootPath, presentation = Presentation.Push, tabIndex = index)
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
            val routerTab = RouterTabLocal(this.viewKey, router, index, this, path)

            tabRouters[index] = routerTab
            router.routerManager[viewKey] = tabRouters[index]!!
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

    override fun get(index: Int): RouterTab = tabRouters[index]!!

    internal fun releaseRouters()
    {
        tabRoutersKeys.forEach { router.routerManager[it.value] = null }
    }

    protected fun showTab(i: Int)
    {
        tabIndex = i
        if (tabChangeCallback != null)
            commandBuffer.apply(Command.ChangeTab(tabChangeCallback!!, i))
    }
}