package com.speakerboxlite.router

import android.util.Log
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.exceptions.ImpossibleRouteException
import com.speakerboxlite.router.exceptions.RouteNotFoundException
import java.util.UUID

enum class RouteType
{
    Simple, Chain, Sub, Dialog, BTS;

    val isSubChain: Boolean get() = this == Sub || this == Dialog || this == BTS
}

data class ViewMeta(val key: String,
                    val routeType: RouteType,
                    val pathName: String)

open class RouterSimple(protected val callerKey: String?,
                        val parent: RouterSimple?,
                        protected val routeManager: RouteManager,
                        protected val routerManager: RouterManager,
                        protected val resultManager: ResultManager,
                        protected val component: Any): Router
{
    protected val pathData = mutableMapOf<String, RoutePath>()

    protected val commandBuffer = CommandBufferImpl()

    override var topRouter: Router?
        get() = routerManager.top
        set(value) { routerManager.top = value }

    protected val viewsStack = mutableListOf<ViewMeta>()

    val isCurrentTop: Boolean get() = parent == null && viewsStack.size == 1

    override fun route(url: String): String?
    {
        val route = routeManager.find(url)
        if (route != null)
        {
            return doRoute(route, RouteType.Sub, route.convert(url), route.preferredPresentation, null)
        }

        return null
    }

    override fun route(path: RoutePath, presentation: Presentation): String =
        route(path, RouteType.Simple, presentation, null)

    override fun <R: Any> routeWithResult(path: RoutePath, presentation: Presentation, result: Result<R>): String =
        route(path, RouteType.Simple, presentation) { result(it as  R) }

    override fun subRoute(path: RoutePath, presentation: Presentation): String =
        route(path, RouteType.Sub, presentation, null)

    override fun <R: Any> subRouteWithResult(path: RoutePath, presentation: Presentation, result: Result<R>): String =
        route(path, RouteType.Sub, presentation) { result(it as  R) }

    override fun chainRoute(path: RoutePath, presentation: Presentation): String =
        route(path, RouteType.Chain, presentation, null)

    override fun <R: Any> chainRouteWithResult(path: RoutePath, presentation: Presentation, result: Result<R>): String =
        route(path, RouteType.Chain, presentation) { result(it as  R) }

    override fun routeDialog(path: RoutePath)
    {
        routeManager.find(path)?.also { commandBuffer.apply(Command.Dialog(createView(it, RouteType.Dialog, path, null))) }
    }

    override fun <R: Any> routeDialogWithResult(path: RoutePath, result: Result<R>)
    {
        routeManager.find(path)?.also { commandBuffer.apply(Command.Dialog(createView(it, RouteType.Dialog, path) { result(it as  R) })) }
    }

    override fun <R : Any> sendResult(result: R)
    {
        resultManager.send(viewsStack.last().key, result)
    }

    override fun back()
    {
        close()
    }

    override fun close()
    {
        val v = viewsStack.last()
        val chain = scanForChain()
        if (chain != null && !v.routeType.isSubChain)
        {
            closeTo(chain.key)
        }
        else
        {
            viewsStack.removeLast()
            dispatchClose(v)
            resultManager.unbind(v.key)
        }
    }

    override fun <R: Any> closeWithResult(result: R)
    {
        val v = viewsStack.last()
        val chain = scanForChain()
        if (chain != null && !v.routeType.isSubChain)
        {
            resultManager.send(chain.key, result)
            closeTo(chain.key)
        }
        else
        {
            viewsStack.removeLast()
            dispatchClose(v)
            resultManager.send(v.key, result)
            resultManager.unbind(v.key)
        }
    }

    override fun closeTo(key: String)
    {
        val i = viewsStack.indexOfFirst { it.key == key }
        if (i != -1)
        {
            _closeTo(i)
        }
        else if (callerKey == null || parent == null)
        {
            _closeTo(0)
        }
        else
        {
            parent.closeTo(key)
            _closeTo(0)
            close()
        }
    }

    override fun closeToTop()
    {
        if (callerKey == null || parent == null)
        {
            _closeTo(0)
        }
        else
        {
            parent.closeToTop()
            _closeTo(0)
            close()
        }
    }

    override fun bindExecutor(executor: CommandExecutor)
    {
        commandBuffer.bind(executor)
    }

    override fun unbindExecutor()
    {
        commandBuffer.unbind()
    }

    override fun onComposeView(view: View<*>)
    {
        val path = pathData[view.viewKey]!!
        val route = routeManager.find(path)
        if (route != null)
            route.onComposeView(view, path, component)

        view.viewModel.router = this
    }

    override fun createRouterLocal(): RouterLocal = RouterLocalImpl(this)

    override fun createRouterTabs(factory: HostViewFactory): RouterTabs = RouterTabsImpl(viewsStack.last().key, factory, this)

    internal open fun createRouter(callerKey: String): Router = RouterSimple(callerKey, this, routeManager, routerManager, resultManager, component)

    internal open fun createRouterTab(callerKey: String, index: Int, tabs: RouterTabsImpl): Router = RouterTab(callerKey, this, routeManager, routerManager, resultManager, component, index, tabs)

    internal fun findRoute(path: RoutePath): RouteController<*>? = routeManager.find(path)

    internal fun setPath(key: String, path: RoutePath)
    {
        pathData[key] = path
    }

    internal fun getPath(key: String): RoutePath = pathData[key]!!

    internal fun bindRouter(view: View<*>)
    {
        routerManager.bind(this, view)
    }

    internal fun scanForChain(): ViewMeta?
    {
        val chain = viewsStack.lastOrNull { it.routeType == RouteType.Chain }
        if (chain != null)
            return chain

        if (parent != null)
            return parent.scanForChain()

        return null
    }


    protected fun _closeTo(i: Int)
    {
        val deleteCount = viewsStack.size - i - 1
        for (j in 0 until deleteCount)
        {
            val v = viewsStack.removeLast()
            resultManager.unbind(v.key)
        }

        commandBuffer.apply(Command.CloseTo(viewsStack.last().key))
    }

    protected fun route(path: RoutePath, routeType: RouteType, presentation: Presentation, result: Result<Any>?): String
    {
        Log.i("", "Start route with path: ${path::class}")
        val route = findRoute(path) ?: throw RouteNotFoundException(path)
        return doRoute(route, routeType, path, presentation, result)
    }

    protected fun doRoute(route: RouteController<*>, routeType: RouteType, path: RoutePath, presentation: Presentation, result: Result<Any>?): String =
        when (presentation)
        {
            Presentation.Modal ->
            {
                val newCallerKey = viewsStack.last().key
                val router = createRouter(newCallerKey)
                val viewKey = when (routeType)
                {
                    RouteType.Simple -> router.route(path = path, presentation = Presentation.Push)
                    RouteType.Sub -> router.subRoute(path = path, presentation = Presentation.Push)
                    RouteType.Chain -> router.chainRoute(path = path, presentation = Presentation.Push)
                    else -> throw ImpossibleRouteException("Modal route can't contains $routeType as ROOT")
                }

                val key = UUID.randomUUID().toString()

                routerManager[key] = router
                commandBuffer.apply(Command.StartModal(key))
                viewKey
            }
            Presentation.Push ->
            {
                val view = createView(route, routeType, path, result)
                commandBuffer.apply(Command.Push(view))
                view.viewKey
            }
            Presentation.BTS ->
            {
                val view = createView(route, routeType, path, result)
                commandBuffer.apply(Command.BottomSheet(view))
                view.viewKey
            }
        }

    protected fun createView(route: RouteController<*>, routeType: RouteType, path: RoutePath, result: Result<Any>?): View<*>
    {
        val view = route.onCreateView()
        view.viewKey = UUID.randomUUID().toString()
        pathData[view.viewKey] = path
        bindRouter(view)

        if (result != null)
        {
            val toKey = if (viewsStack.isEmpty()) callerKey else viewsStack.last().key
            if (toKey != null)
                resultManager.bind(view.viewKey, toKey, result)
        }

        viewsStack.add(ViewMeta(view.viewKey, routeType, path::class.toString()))

        return view
    }

    protected fun dispatchClose(v: ViewMeta)
    {
        when (v.routeType)
        {
            RouteType.BTS -> commandBuffer.apply(Command.CloseBottomSheet(v.key))
            RouteType.Dialog -> commandBuffer.apply(Command.CloseDialog(v.key))
            else -> commandBuffer.apply(Command.Close)
        }
    }
}
