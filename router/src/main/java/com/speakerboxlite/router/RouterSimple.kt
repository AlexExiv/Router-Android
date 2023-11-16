package com.speakerboxlite.router

import android.util.Log
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.controllers.RouteControllerComposable
import com.speakerboxlite.router.controllers.RouteControllerInterface
import com.speakerboxlite.router.controllers.RouteParamsGen
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

    val isNoStackStructure: Boolean get() = this == Dialog || this == BTS
}

data class ViewMeta(val key: String,
                    val routeType: RouteType,
                    val route: RouteControllerInterface<RoutePath, *>,
                    val path: KClass<*>,
                    val result: Result<Any>?,
                    var lockBack: Boolean = false)
{
    override fun toString(): String
    {
        return "(key=$key, routeType=$routeType, route=${route::class.qualifiedName}, path=${path.qualifiedName}, result=$result, lockBack=$lockBack)"
    }
}

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

    override var lockBack: Boolean
        get() = viewsStack.lastOrNull()?.lockBack ?: false
        set(value) { viewsStack.lastOrNull()?.lockBack = value }

    internal val viewsStack = mutableListOf<ViewMeta>()
    protected val viewsStackById = mutableMapOf<String, ViewMeta>()

    val isCurrentTop: Boolean get() = parent == null && viewsStack.size == 1

    protected var isClosing = false

    protected val routerTabsByKey = mutableMapOf<String, RouterTabsImpl>()
    internal var rootPath: RoutePath? = null
        private set

    override fun route(url: String): String?
    {
        val route = routeManager.find(url)
        if (route != null)
            return route(route.convert(url), RouteType.Simple, route.preferredPresentation, null)

        return null
    }

    override fun route(path: RoutePath, presentation: Presentation?): String =
        route(path, RouteType.Simple, presentation, null)

    override fun <R: Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): String =
        route(path, RouteType.Simple, presentation) { result(it as  R) }

    override fun replace(path: RoutePath): String
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isReplace = true)
        if (tryRouteMiddlewares(routeParams, route))
            return ""

        popViewStack()

        val view = createView(route, RouteType.Simple, path, null)
        commandBuffer.apply(Command.Replace(path, view, route.animationController()))

        return view.viewKey
    }

    override fun routeDialog(path: RoutePath)
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isDialog = true)
        if (tryRouteMiddlewares(routeParams, route))
            return

        commandBuffer.apply(Command.Dialog(createView(route, RouteType.Dialog, path, null)))
    }

    override fun <R: Any> routeDialogWithResult(path: RoutePathResult<R>, result: Result<R>)
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isDialog = true) { result(it as R) }
        if (tryRouteMiddlewares(routeParams, route)  )
            return

        commandBuffer.apply(Command.Dialog(createView(route, RouteType.Dialog, path) { result(it as  R) }))
    }

    override fun routeBTS(path: RoutePath)
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isBts = true)
        if (tryRouteMiddlewares(routeParams, route))
            return

        commandBuffer.apply(Command.BottomSheet(createView(route, RouteType.BTS, path, null)))
    }

    override fun <R : Any> routeBTSWithResult(path: RoutePathResult<R>, result: Result<R>)
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isBts = true) { result(it as R) }
        if (tryRouteMiddlewares(routeParams, route))
            return

        commandBuffer.apply(Command.BottomSheet(createView(route, RouteType.BTS, path) { result(it as  R) }))
    }

    override fun route(path: RouteParamsGen)
    {
        if (path.isDialog)
        {
            if (path.result == null)
                routeDialog(path.path)
            else
                routeDialogWithResult(path.path as RoutePathResult<Any>, path.result)
        }
        else if (path.isBts)
        {
            if (path.result == null)
                routeBTS(path.path)
            else
                routeBTSWithResult(path.path as RoutePathResult<Any>, path.result)
        }
        else if (path.isReplace)
        {
            replace(path.path)
        }
        else
        {
            if (path.result == null)
                route(path.path, path.presentation)
            else
                routeWithResult(path.path as RoutePathResult<Any>, path.presentation, path.result)
        }
    }

    override fun back()
    {
        if (lockBack || !hasPreviousScreen)
            return

        val v = popViewStack() ?: return
        dispatchClose(v)

        if (pathData[v.key] != null)
            tryCloseMiddlewares(pathData[v.key]!!)

        tryRepeatTopIfEmpty()
    }

    override fun close()
    {
        val v = viewsStack.lastOrNull() ?: return
        val chain = scanForChain()
        if (chain != null && chain.key != v.key && chain.route.isPartOfChain(v.path))
        {
            closeTo(chain.key)
            close()
        }
        else
        {
            popViewStack()
            dispatchClose(v)

            if (pathData[v.key] != null)
                tryCloseMiddlewares(pathData[v.key]!!)
        }

        tryRepeatTopIfEmpty()
    }

    protected open fun unbind(key: String)
    {
        resultManager.unbind(key)
        pathData.remove(key)
        routerTabsByKey.remove(key)
        unbindRouter(key)
/*
        if (isClosing)
            releaseRouter()*/
    }

    override fun closeTo(key: String)
    {
        var toIndex = -1
        for (i in viewsStack.indices)
        {
            if (viewsStack[i].key == key)
            {
                toIndex = i
                break
            }

            if (routerTabsByKey[viewsStack[i].key]?.closeTabsTo(key) == true)
            {
                if (i == (viewsStack.size - 1))
                    return

                toIndex = i
                break
            }
        }

        if (toIndex != -1)
        {
            _closeTo(toIndex)
        }
        else if (callerKey == null || parent == null)
        {
            _closeTo(0)
        }
        else
        {
            _closeAll()
            parent.closeTo(key)
        }

        tryRepeatTopIfEmpty()
    }

    override fun closeToTop()
    {
        if (callerKey == null || parent == null)
        {
            _closeTo(0)
        }
        else
        {
            _closeAll()
            parent.closeToTop()
        }

        tryRepeatTopIfEmpty()
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
        val route = findRoute(path)

        (route as? RouteControllerComposable<RoutePath, View>)?.also {
            it.onComposeView(this, view, path)
        }

        view.resultProvider = RouterResultProviderImpl(view.viewKey, resultManager)
    }

    override fun onComposeAnimation(view: View)
    {
        val path = pathData[view.viewKey]!!
        val route = findRoute(path)

        route.animationController()?.onConfigureView(path, view)
    }

    override fun createRouterLocal(key: String): RouterLocal = RouterLocalImpl(key, this)

    override fun createRouterTabs(key: String, presentInTab: Boolean): RouterTabs
    {
        if (routerTabsByKey[key] == null)
            routerTabsByKey[key] = RouterTabsImpl(viewsStack.lastOrNull()?.key ?: "", this, presentInTab)

        return routerTabsByKey[key]!!
    }

    override fun removeView(key: String)
    {
        viewsStack.removeAll { it.key == key }
        if (viewsStack.isEmpty() && parent != null)
            isClosing = true

        unbind(key)

        tryRepeatTopIfEmpty()
    }

    internal open fun createRouter(callerKey: String): Router = RouterSimple(callerKey, this, routeManager, routerManager, resultManager)

    internal open fun createRouterTab(callerKey: String, index: Int, tabs: RouterTabsImpl): Router = RouterTab(callerKey, this, routeManager, routerManager, resultManager, index, tabs)

    internal fun findRoute(path: RoutePath): RouteControllerInterface<RoutePath, *> = routeManager.find(path) ?: throw RouteNotFoundException(path)

    internal fun setPath(key: String, path: RoutePath)
    {
        pathData[key] = path
    }

    internal fun getPath(key: String): RoutePath = pathData[key]!!

    internal fun bindRouter(viewKey: String)
    {
        routerManager.bindView(this, viewKey)
    }

    internal fun unbindRouter(viewKey: String)
    {
        routerManager.unbindView(viewKey)
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
        for (v in viewsStack)
        {
            if (v.path == clazz)
                return v

            val vs = routerTabsByKey[v.key]?.scanForPath(clazz)
            if (vs != null)
                return vs
        }

        if (parent != null && recursive)
            return parent.scanForPath(clazz)

        return null
    }

    internal fun containsView(key: String): Boolean =
        viewsStack.lastOrNull { it.key == key } != null

    internal fun tryRouteMiddlewares(params: RouteParamsGen, route: RouteControllerInterface<RoutePath, *>): Boolean
    {
        //onBeforeRoute only available for not empty viewsStack because it refers to current route
        if (viewsStack.isNotEmpty())
        {
            val curPath = pathData[viewsStack.last().key]!!
            val curRoute = findRoute(curPath)
            if (curRoute.onBeforeRoute(this, curPath, params))
                return true

            for (mid in curRoute.middlewares)
            {
                if (mid.onBeforeRoute(this, curPath, params))
                    return true
            }
        }

        //skip if it's not a root router and viewsStack is empty because the caller has already dispatched middlewares
        if (parent == null || viewsStack.isNotEmpty())
        {
            if (route.onRoute(this, viewsStack.lastOrNull()?.let { pathData[it.key] }, params))
                return true

            for (mid in route.middlewares)
            {
                if (mid.onRoute(this, viewsStack.lastOrNull()?.let { pathData[it.key] }, params))
                    return true
            }
        }

        return false
    }

    internal fun tryCloseMiddlewares(path: RoutePath)
    {
        val router = if (viewsStack.isEmpty() && parent != null) parent else this
        val prev = if (viewsStack.isEmpty() && parent != null)
            parent.viewsStack.lastOrNull()?.let { parent.pathData[it.key] }
        else
            viewsStack.lastOrNull()?.let { pathData[it.key] }

        val route = findRoute(path)!!

        if (route.onClose(router, path, prev))
            return

        for (mid in route.middlewares)
        {
            if (mid.onClose(router, path, prev))
                return
        }
    }

    /**
     * Try to repeat root path if root router has become empty due to some troubles
     */
    protected fun tryRepeatTopIfEmpty()
    {
        if (viewsStack.isEmpty() && parent == null && rootPath != null)
            route(rootPath!!, RouteType.Simple, Presentation.Push, null)
    }

    protected fun _closeTo(i: Int)
    {
        if (viewsStack.isNotEmpty() && viewsStack.last().routeType.isNoStackStructure)
            close()

        val deleteCount = viewsStack.size - i - 1
        for (j in 0 until deleteCount)
            popViewStack()

        commandBuffer.apply(Command.CloseTo(viewsStack.last().key))
    }

    protected fun _closeAll()
    {
        if (viewsStack.isNotEmpty() && viewsStack.last().routeType.isNoStackStructure)
            close()

        val count = viewsStack.size
        for (i in 0 until count)
            popViewStack()

        commandBuffer.apply(Command.CloseAll)
    }

    protected fun route(path: RoutePath, routeType: RouteType, presentation: Presentation?, result: Result<Any>?): String
    {
        Log.i("Router", "Start route with path: ${path::class}")

        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, presentation = presentation, result = result)
        if (tryRouteMiddlewares(routeParams, route))
            return ""

        if (viewsStack.isNotEmpty() && viewsStack.last().routeType.isNoStackStructure)
            close()

        if (isClosing)
        {
            return if (result == null)
                parent!!.route(path, presentation)
            else
                parent!!.routeWithResult(path as RoutePathResult<Any>, presentation, result)
        }

        if (route.singleTop)
        {
            val exist = scanForPath(path::class)
            if (exist != null)
            {
                closeTo(exist.key)
                return exist.key
            }
        }

        return doRoute(route, routeType, path, presentation ?: route.preferredPresentation, result)
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
                    RouteType.Simple ->
                    {
                        if (result == null)
                            router.route(path = path, presentation = Presentation.Push)
                        else
                            router.routeWithResult(path = path as RoutePathResult<Any>, presentation = Presentation.Push, result = result)
                    }

                    else -> throw ImpossibleRouteException("Modal route can't contains $routeType as ROOT")
                }

                val key = UUID.randomUUID().toString()

                routerManager[key] = router
                commandBuffer.apply(Command.StartModal(key, route.params))
                viewKey
            }
            Presentation.Push ->
            {
                val view = createView(route, routeType, path, result)
                commandBuffer.apply(Command.Push(path, view, route.animationController()))
                view.viewKey
            }
        }

    protected fun createView(route: RouteControllerInterface<RoutePath, *>, routeType: RouteType, path: RoutePath, result: Result<Any>?): View
    {
        val view = route.onCreateView(path)
        view.viewKey = UUID.randomUUID().toString()
        pathData[view.viewKey] = path
        bindRouter(view.viewKey)

        val chain = scanForChain()
        val toKey = if (chain != null && chain.route.isPartOfChain(path::class))
            chain.key
        else if (viewsStack.isEmpty())
            callerKey
        else
            viewsStack.last().key

        if (toKey != null)
            bindResult(view.viewKey, toKey, if (chain != null && chain.route.isPartOfChain(path::class)) chain.result else result)

        if (viewsStack.isEmpty())
            rootPath = path

        val meta = ViewMeta(view.viewKey, routeType, route, path::class, result)
        viewsStack.add(meta)
        viewsStackById[meta.key] = meta

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

    protected fun popViewStack(): ViewMeta?
    {
        if (viewsStack.isEmpty())
            return null

        val v = viewsStack.removeLast()

        if (viewsStack.isEmpty() && parent != null)
            isClosing = true

        return v
    }

    internal fun buildViewStackPath(): List<ViewMeta>
    {
        var prev: RouterSimple? = this
        val totalStack = mutableListOf<ViewMeta>()
        while (prev != null)
        {
            totalStack.addAll(0, prev.viewsStack)
            prev = prev.parent
        }

        return totalStack
    }
}
