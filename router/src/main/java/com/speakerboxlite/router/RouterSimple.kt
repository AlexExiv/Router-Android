package com.speakerboxlite.router

import android.util.Log
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.controllers.RouteControllerComposable
import com.speakerboxlite.router.controllers.RouteControllerInterface
import com.speakerboxlite.router.exceptions.ImpossibleRouteException
import com.speakerboxlite.router.exceptions.RouteNotFoundException
import com.speakerboxlite.router.result.ResultManager
import com.speakerboxlite.router.result.RouterResultProvider
import com.speakerboxlite.router.result.RouterResultProviderImpl
import java.util.UUID
import kotlin.reflect.KClass

enum class RouteType
{
    Simple, Dialog, BTS;
}

data class ViewMeta(val key: String,
                    val routeType: RouteType,
                    val route: RouteControllerInterface<RoutePath, *>,
                    val path: KClass<*>,
                    val result: Result<Any>?)

open class RouterSimple(protected val callerKey: String?,
                        val parent: RouterSimple?,
                        protected val routeManager: RouteManager,
                        protected val routerManager: RouterManager,
                        protected val resultManager: ResultManager): Router
{
    protected val pathData = mutableMapOf<String, RoutePath>()

    protected val commandBuffer: CommandBuffer = CommandBufferImpl()

    override var topRouter: Router?
        get() = routerManager.top
        set(value) { routerManager.top = value }

    override val hasPreviousScreen: Boolean get() = parent != null || viewsStack.size > 1

    protected val viewsStack = mutableListOf<ViewMeta>()

    val isCurrentTop: Boolean get() = parent == null && viewsStack.size == 1

    override fun route(url: String): String?
    {
        val route = routeManager.find(url)
        if (route != null)
        {
            return route(route.convert(url), RouteType.Simple, route.preferredPresentation, null)
        }

        return null
    }

    override fun route(path: RoutePath, presentation: Presentation): String =
        route(path, RouteType.Simple, presentation, null)

    override fun <R: Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation, result: Result<R>): String =
        route(path, RouteType.Simple, presentation) { result(it as  R) }

    override fun routeDialog(path: RoutePath)
    {
        routeManager.find(path)?.also { commandBuffer.apply(Command.Dialog(createView(it, RouteType.Dialog, path, null))) }
    }

    override fun <R: Any> routeDialogWithResult(path: RoutePathResult<R>, result: Result<R>)
    {
        routeManager.find(path)?.also { commandBuffer.apply(Command.Dialog(createView(it, RouteType.Dialog, path) { result(it as  R) })) }
    }

    override fun back()
    {
        val v = viewsStack.last()
        viewsStack.removeLast()
        dispatchClose(v)
        unbind(v.key)
    }

    override fun close()
    {
        val v = viewsStack.last()
        val chain = scanForChain()
        if (chain != null && chain.key != v.key && chain.route.isPartOfChain(v.path))
        {
            closeTo(chain.key)
            close()
        }
        else
        {
            viewsStack.removeLast()
            dispatchClose(v)
            unbind(v.key)
        }
    }

    protected open fun unbind(key: String)
    {
        resultManager.unbind(key)
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

    override fun onComposeView(view: View)
    {
        val path = pathData[view.viewKey]!!
        val route = routeManager.find(path) ?: throw RouteNotFoundException(path)

        (route as? RouteControllerComposable<RoutePath, View>)?.also {
            it.onComposeView(this, view, path)
        }

        view.resultProvider = RouterResultProviderImpl(view.viewKey, resultManager)
    }

    override fun createRouterLocal(key: String): RouterLocal = RouterLocalImpl(key, this)

    override fun createRouterTabs(factory: HostViewFactory): RouterTabs = RouterTabsImpl(viewsStack.last().key, factory, this)

    override fun removeView(key: String)
    {
        viewsStack.removeAll { it.key == key }
        unbind(key)
    }

    internal open fun createRouter(callerKey: String): Router = RouterSimple(callerKey, this, routeManager, routerManager, resultManager)

    internal open fun createRouterTab(callerKey: String, index: Int, tabs: RouterTabsImpl): Router = RouterTab(callerKey, this, routeManager, routerManager, resultManager, index, tabs)

    internal fun findRoute(path: RoutePath): RouteControllerInterface<RoutePath, *>? = routeManager.find(path)

    internal fun setPath(key: String, path: RoutePath)
    {
        pathData[key] = path
    }

    internal fun getPath(key: String): RoutePath = pathData[key]!!

    internal fun bindRouter(view: View)
    {
        routerManager.bind(this, view)
    }

    override fun createResultProvider(key: String): RouterResultProvider = RouterResultProviderImpl(key, resultManager)

    internal fun bindResult(from: String, to: String, result: Result<Any>?)
    {
        resultManager.bind(from, to, result)
    }

    internal fun scanForChain(): ViewMeta?
    {
        val chain = viewsStack.lastOrNull { it.route.isChain }
        if (chain != null)
            return chain

        if (parent != null)
            return parent.scanForChain()

        return null
    }

    internal open fun scanForPath(clazz: KClass<*>, recursive: Boolean = true): ViewMeta?
    {
        val v = viewsStack.lastOrNull { it.path == clazz }
        if (v != null)
            return v

        if (parent != null && recursive)
            return parent.scanForPath(clazz)

        return null
    }

    internal fun containsView(key: String): Boolean =
        viewsStack.lastOrNull { it.key == key } != null

    protected fun _closeTo(i: Int)
    {
        val deleteCount = viewsStack.size - i - 1
        for (j in 0 until deleteCount)
        {
            val v = viewsStack.removeLast()
            unbind(v.key)
        }

        commandBuffer.apply(Command.CloseTo(viewsStack.last().key))
    }

    protected fun route(path: RoutePath, routeType: RouteType, presentation: Presentation, result: Result<Any>?): String
    {
        Log.i("", "Start route with path: ${path::class}")
        val route = findRoute(path) ?: throw RouteNotFoundException(path)
        if (route.singleton)
        {
            val exist = scanForPath(path::class)
            if (exist != null)
            {
                closeTo(exist.key)
                return exist.key
            }
        }

        return doRoute(route, routeType, path, presentation, result)
    }

    protected fun doRoute(route: RouteControllerInterface<RoutePath, *>, routeType: RouteType, path: RoutePath, presentation: Presentation, result: Result<Any>?): String =
        when (presentation)
        {
            Presentation.Modal ->
            {
                val newCallerKey = viewsStack.last().key
                val router = createRouter(newCallerKey)
                val viewKey = when (routeType)
                {
                    RouteType.Simple -> router.route(path = path, presentation = Presentation.Push)
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

    protected fun createView(route: RouteControllerInterface<RoutePath, *>, routeType: RouteType, path: RoutePath, result: Result<Any>?): View
    {
        val view = route.onCreateView(path)
        view.viewKey = UUID.randomUUID().toString()
        pathData[view.viewKey] = path
        bindRouter(view)

        val chain = scanForChain()
        val toKey = if (chain != null && chain.route.isPartOfChain(path::class))
            chain.key
        else if (viewsStack.isEmpty())
            callerKey
        else
            viewsStack.last().key

        if (toKey != null)
            bindResult(view.viewKey, toKey, if (chain != null && chain.route.isPartOfChain(path::class)) chain.result else result)

        viewsStack.add(ViewMeta(view.viewKey, routeType, route, path::class, result))

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
