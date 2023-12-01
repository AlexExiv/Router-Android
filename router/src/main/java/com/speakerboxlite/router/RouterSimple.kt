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

    override fun route(url: String): Router?
    {
        val route = routeManager.find(url)
        if (route != null)
            return route(null, route.convert(url), RouteType.Simple, route.preferredPresentation, null, null)

        return null
    }

    override fun route(path: RoutePath, presentation: Presentation?): Router? =
        route(null, path, RouteType.Simple, presentation, null, null)

    override fun <R: Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): Router? =
        route(null, path, RouteType.Simple, presentation, null) { result(it as  R) }

    override fun replace(path: RoutePath): Router?
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isReplace = true)
        if (tryRouteMiddlewares(routeParams, route))
            return null

        popViewStack()

        val view = createView(route, RouteType.Simple, path, null, null)
        commandBuffer.apply(Command.Replace(path, view, route.animationController()))

        return this
    }

    override fun routeDialog(path: RoutePath): Router?
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isDialog = true)
        if (tryRouteMiddlewares(routeParams, route))
            return null

        commandBuffer.apply(Command.Dialog(createView(route, RouteType.Dialog, path, null, null)))

        return this
    }

    override fun <R: Any> routeDialogWithResult(path: RoutePathResult<R>, result: Result<R>): Router?
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isDialog = true) { result(it as R) }
        if (tryRouteMiddlewares(routeParams, route)  )
            return null

        commandBuffer.apply(Command.Dialog(createView(route, RouteType.Dialog, path, null) { result(it as  R) }))

        return this
    }

    override fun routeBTS(path: RoutePath): Router?
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isBts = true)
        if (tryRouteMiddlewares(routeParams, route))
            return null

        commandBuffer.apply(Command.BottomSheet(createView(route, RouteType.BTS, path, null, null)))

        return this
    }

    override fun <R : Any> routeBTSWithResult(path: RoutePathResult<R>, result: Result<R>): Router?
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isBts = true) { result(it as R) }
        if (tryRouteMiddlewares(routeParams, route))
            return null

        commandBuffer.apply(Command.BottomSheet(createView(route, RouteType.BTS, path, null) { result(it as  R) }))

        return this
    }

    override fun route(path: RouteParamsGen): Router?
    {
        return if (path.execRouter != null && path.execRouter !== this)
        {
            path.execRouter.route(path)
        }
        else if (path.isDialog)
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
        else if (path.tabIndex != null)
        {
            val r = routerTabsByKey[viewsStack.lastOrNull()?.key]
            r?.route(path.tabIndex)
            r?.get(path.tabIndex)
        }
        else
        {
            if (path.result == null)
                route(path.path, path.presentation)
            else
                routeWithResult(path.path as RoutePathResult<Any>, path.presentation, path.result)
        }
    }

    override fun back(): Router?
    {
        if (lockBack || !hasPreviousScreen)
            return this

        val v = popViewStack() ?: return (parent ?: this)
        dispatchClose(v)
/*
        if (pathData[v.key] != null)
            tryCloseMiddlewares(pathData[v.key]!!)
*/
        tryRepeatTopIfEmpty()
        return if (isClosing) parent else this
    }

    override fun close(): Router?
    {
        val v = viewsStack.lastOrNull() ?: return (parent ?: this)
        val chain = scanForChain()
        val returnRouter = if (chain != null && chain.key != v.key && chain.route.isPartOfChain(v.path))
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

            if (isClosing) parent else this
        }

        tryRepeatTopIfEmpty()
        return returnRouter
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

    override fun closeTo(key: String): Router?
    {
        var toIndex = -1
        var returnRouter: Router? = null
        for (i in viewsStack.indices)
        {
            if (viewsStack[i].key == key)
            {
                toIndex = i
                break
            }

            val routerTabs = routerTabsByKey[viewsStack[i].key]
            if (routerTabs != null && routerTabs.closeTabsTo(key))
            {
                if (i == (viewsStack.size - 1))
                    return this

                toIndex = i
                returnRouter = routerTabs[i]
                break
            }
        }

        if (toIndex != -1)
        {
            val r = _closeTo(toIndex)
            if (returnRouter == null)
                returnRouter = r
        }
        else if (callerKey == null || parent == null)
        {
            returnRouter = _closeTo(0)
        }
        else
        {
            _closeAll()
            returnRouter = parent.closeTo(key)
        }

        tryRepeatTopIfEmpty()
        return returnRouter
    }

    override fun closeToTop(): Router?
    {
        val returnRouter = if (callerKey == null || parent == null)
        {
            _closeTo(0)
        }
        else
        {
            _closeAll()
            parent.closeToTop()
        }

        tryRepeatTopIfEmpty()
        return returnRouter
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

        val route = findRoute(path)

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
            route(null, rootPath!!, RouteType.Simple, Presentation.Push, null, null)
    }

    internal fun _closeTo(i: Int): Router?
    {
        closeAllNoStack()

        val deleteCount = viewsStack.size - i - 1
        for (j in 0 until deleteCount)
            popViewStack()

        commandBuffer.apply(Command.CloseTo(viewsStack.last().key))

        return this
    }

    internal fun _closeAll(): Router?
    {
        closeAllNoStack()

        val count = viewsStack.size
        for (i in 0 until count)
            popViewStack()

        commandBuffer.apply(Command.CloseAll)

        return parent ?: this
    }

    internal fun closeAllNoStack()
    {
        while (viewsStack.isNotEmpty() && viewsStack.last().routeType.isNoStackStructure)
            close()
    }

    internal fun route(execRouter: Router?, path: RoutePath, routeType: RouteType, presentation: Presentation?, resultKey: String?, result: Result<Any>?): Router?
    {
        Log.i("Router", "Start route with path: ${path::class}")

        val route = findRoute(path)

        //try to check all middlewares
        val routeParams = RouteParamsGen(execRouter = execRouter, path = path, presentation = presentation, result = result)
        if (tryRouteMiddlewares(routeParams, route))
            return null

        closeAllNoStack()

        //if last view has tabs try to route to its tab if it has in the root the same path
        val tabRouter = tryRouteToTab(path)
        if (tabRouter != null)
            return  tabRouter

        //if this router in the Closing state pass this route to its path for the execution
        if (isClosing)
        {
            return if (result == null)
                parent!!.route(path, presentation)
            else
                parent!!.routeWithResult(path as RoutePathResult<Any>, presentation, result)
        }

        //if this route has singleTop flag try to find it in the hierarchy and route to the instance
        if (route.singleTop)
        {
            val exist = scanForPath(path::class)
            if (exist != null)
                return closeTo(exist.key)
        }

        //go to route
        return doRoute(route, routeType, path, presentation ?: route.preferredPresentation, resultKey, result)
    }

    internal open fun tryRouteToTab(path: RoutePath): Router?
    {
        val tabRouter = routerTabsByKey[viewsStack.lastOrNull()?.key]
        if (tabRouter != null)
        {
            val i = tabRouter.containsPath(path::class)
            if (i != null)
            {
                tabRouter.route(i)
                return tabRouter[i]
            }
        }

        return null
    }

    protected fun doRoute(route: RouteControllerInterface<RoutePath, *>, routeType: RouteType, path: RoutePath, presentation: Presentation, resultKey: String?, result: Result<Any>?): Router? =
        when (presentation)
        {
            Presentation.ModalNewTask ->
            {
                val newCallerKey = viewsStack.last().key
                val router = createRouter(newCallerKey)
                val returnRouter = when (routeType)
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
                returnRouter
            }
            Presentation.Push, Presentation.Modal ->
            {
                val view = createView(route, routeType, path, resultKey, result)
                commandBuffer.apply(Command.Push(path, view, route.animationController()))
                this
            }
        }

    protected fun createView(route: RouteControllerInterface<RoutePath, *>, routeType: RouteType, path: RoutePath, resultKey: String?, result: Result<Any>?): View
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
        else if (resultKey != null)
            resultKey
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
