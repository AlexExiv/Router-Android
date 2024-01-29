package com.speakerboxlite.router

import android.util.Log
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.RouteType
import com.speakerboxlite.router.annotations.TabUnique
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.controllers.RouteControllerComposable
import com.speakerboxlite.router.controllers.RouteControllerInterface
import com.speakerboxlite.router.controllers.RouteControllerViewModelHolder
import com.speakerboxlite.router.controllers.RouteControllerViewModelProvider
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.exceptions.ImpossibleRouteException
import com.speakerboxlite.router.exceptions.RouteNotFoundException
import com.speakerboxlite.router.result.ResultManager
import com.speakerboxlite.router.result.RouterResultProvider
import com.speakerboxlite.router.result.RouterResultProviderImpl
import com.speakerboxlite.router.result.ViewResultType
import java.lang.ref.WeakReference
import java.util.UUID
import kotlin.reflect.KClass

data class ViewMeta(val key: String,
                    val routeType: RouteType,
                    val isCompose: Boolean,
                    val route: RouteControllerInterface<RoutePath, *>,
                    val path: KClass<*>,
                    var lockBack: Boolean = false)
{
    override fun toString(): String
    {
        return "(key=$key, routeType=$routeType, route=${route::class.qualifiedName}, path=${path.qualifiedName}, lockBack=$lockBack)"
    }
}

data class ViewResultData(val toKey: String,
                          val resultType: ViewResultType,
                          val result: RouterResultDispatcher<ViewResult, Any>)
{
    companion object
    {
        fun <VR: ViewResult, R: Any> create(viewResult: VR, result: RouterResultDispatcher<VR, R>) =
            ViewResultData(viewResult.resultProvider.key, ViewResultType.fromViewResult(viewResult), result as RouterResultDispatcher<ViewResult, Any>)
    }
}

internal interface RouterInternal
{
    fun routeInternal(execRouter: Router?, path: RoutePath, routeType: RouteType, presentation: Presentation?, viewResult: ViewResultData?): Router?
}

open class RouterSimple(protected val callerKey: String?,
                        parent: RouterSimple?,
                        protected val routeManager: RouteManager,
                        internal val routerManager: RouterManager,
                        protected val resultManager: ResultManager): Router, RouterInternal
{
    protected val pathData = mutableMapOf<String, RoutePath>()

    protected val commandBuffer: CommandBuffer = CommandBufferImpl()

    private var weakParent = WeakReference(parent)
    val parent: RouterSimple? get() = weakParent.get()

    private var weakChild = WeakReference<RouterSimple>(null)
    val child: RouterSimple? get() = weakChild.get()

    override val topRouter: Router? get() = routerManager.top

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

    init
    {
        parent?.weakChild = WeakReference(this)
    }

    override fun route(url: String): Router?
    {
        val route = routeManager.find(url)
        if (route != null)
            return routeInternal(null, route.convert(url), RouteType.Simple, route.preferredPresentation, null)

        return null
    }

    override fun route(path: RoutePath, presentation: Presentation?): Router? =
        routeInternal(null, path, RouteType.Simple, presentation, null)

    override fun <VR: ViewResult, R: Any> routeWithResult(viewResult: VR, path: RoutePathResult<R>, presentation: Presentation?, result: RouterResultDispatcher<VR, R>): Router? =
        routeInternal(null, path, RouteType.Simple, presentation, ViewResultData.create(viewResult, result))

    override fun replace(path: RoutePath): Router?
    {
        val route = findRoute(path)
        val routeParams = RouteParamsGen(path = path, isReplace = true)
        if (tryRouteMiddlewares(routeParams, route))
            return null

        popViewStack()

        val view = createView(route, RouteType.Simple, path, null)
        routerManager.push(view.viewKey, this)
        commandBuffer.apply(Command.Replace(path, view, route.animationController(null, view)))

        return this
    }

    override fun route(path: RouteParamsGen): Router?
    {
        return if (path.execRouter != null && path.execRouter !== this)
        {
            path.execRouter.route(path)
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
                routeInternal(null, path.path as RoutePathResult<Any>, RouteType.Simple, path.presentation, path.result)
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
        return if (isClosing) parent else this //routerManager.pop()//
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
        routerManager.remove(key)
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
            returnRouter = parent?.closeTo(key)
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
            parent?.closeToTop()
        }

        tryRepeatTopIfEmpty()
        return returnRouter
    }

    override fun bindExecutor(executor: CommandExecutor)
    {
        commandBuffer.bind(executor)
        syncExecutor()
    }

    override fun unbindExecutor()
    {
        commandBuffer.unbind()
    }

    protected fun syncExecutor()
    {
        val isClosed = isClosing
        val remove = commandBuffer.sync(viewsStack.map { it.key })
        remove.forEach { removeView(it) }

        if (!isClosed && viewsStack.isEmpty() && rootPath != null)
        {
            println("|------RESTART ROUTER------|")
            isClosing = false
            routeInternal(null, rootPath!!, RouteType.Simple, Presentation.Push, null)
        }
    }

    override fun onPrepareView(view: View, viewModel: ViewModel?)
    {
        val path = pathData[view.viewKey]!!
        val route = findRoute(path)

        (route as? RouteControllerComposable<RoutePath, View>)?.onPrepareView(this, view, path)

        if (viewModel != null)
        {
            (route as? RouteControllerViewModelHolder<ViewModel>)?.onPrepareViewModel(this, view.viewKey, viewModel)
        }

        if (view is ViewResult)
            view.resultProvider = RouterResultProviderImpl(view.viewKey, resultManager)
    }

    override fun <VM : ViewModel> provideViewModel(view: View, modelProvider: RouterModelProvider): VM
    {
        val path = pathData[view.viewKey]!!
        val route = findRoute(path)
        route as? RouteControllerViewModelProvider<RoutePath, VM> ?: error("${route::class} is not a RouteControllerViewModelProvider")
        return route.onProvideViewModel(modelProvider, path)
    }

    override fun onComposeAnimation(view: View)
    {
        val path = pathData[view.viewKey]!!
        val route = findRoute(path)

        //route.animationController()?.onConfigureView(path, view)
    }

    override fun createRouterLocal(key: String): RouterLocal = RouterLocalImpl(key, this)

    override fun createRouterTabs(key: String): RouterTabs = createRouterTabs(key, null, true)

    internal fun createRouterTabs(key: String, tabRouteInParent: Boolean? = null, createReel: Boolean): RouterTabs
    {
        if (routerTabsByKey[key] == null)
        {
            val tabProps = viewsStackById[key]!!.route.tabProps ?: error("Tab props has not been specified. Use Tab annotation to specify props")
            val _tabRouteInParent = tabRouteInParent ?: tabProps.tabRouteInParent

            routerTabsByKey[key] = RouterTabsImpl(key, viewsStack.lastOrNull()?.key ?: "", this, _tabRouteInParent || !createReel, tabProps.tabUnique)
            if (createReel)
                routerManager.pushReel(key, routerTabsByKey[key]!!)
        }

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

    internal open fun createRouterTab(callerKey: String, index: Int, tabs: RouterTabsImpl): Router = RouterTabSimple(callerKey, this, routeManager, routerManager, resultManager, index, tabs)

    internal fun findRoute(path: RoutePath): RouteControllerInterface<RoutePath, *> = routeManager.find(path) ?: throw RouteNotFoundException(path)

    internal fun setPath(key: String, path: RoutePath)
    {
        pathData[key] = path
    }

    internal fun getPath(key: String): RoutePath = pathData[key]!!

    internal fun bindRouter(viewKey: String)
    {
        routerManager[viewKey] = this
    }

    internal fun unbindRouter(viewKey: String)
    {
        routerManager[viewKey] = null
    }

    override fun createResultProvider(key: String): RouterResultProvider = RouterResultProviderImpl(key, resultManager)

    internal fun bindResult(from: String, result: ViewResultData)
    {
        resultManager.bind(from, result.toKey, result.resultType, result.result)
    }

    internal fun scanForChain(): ViewMeta?
    {
        val chain = viewsStack.lastOrNull { it.route.isChain }
        if (chain != null)
            return chain

        if (parent != null)
            return parent?.scanForChain()

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
            return parent?.scanForPath(clazz)

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
        val _parent = parent
        val router = if (viewsStack.isEmpty() && _parent != null) _parent else this
        val prev = if (viewsStack.isEmpty() && _parent != null)
            _parent.viewsStack.lastOrNull()?.let { _parent.pathData[it.key] }
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
            routeInternal(null, rootPath!!, RouteType.Simple, Presentation.Push, null)
    }

    internal fun _closeTo(i: Int): Router?
    {
        closeAllNoStack()

        if (i == (viewsStack.size - 1))
            return this

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

    internal fun closeAllNoStack(): Boolean
    {
        var was = false
        while (viewsStack.isNotEmpty() && viewsStack.last().routeType.isNoStackStructure)
        {
            close()
            was = true
        }

        return was
    }

    override fun routeInternal(execRouter: Router?, path: RoutePath, routeType: RouteType, presentation: Presentation?, viewResult: ViewResultData?): Router?
    {
        Log.i("Router", "Start route with path: ${path::class}")

        val route = findRoute(path)

        //try to check all middlewares
        val routeParams = RouteParamsGen(execRouter = execRouter, path = path, presentation = presentation, result = viewResult)
        if (tryRouteMiddlewares(routeParams, route))
            return null

        when (route.routeType)
        {
            RouteType.Dialog, RouteType.BTS ->
            {
                return doDialogRoute(route, path, viewResult)
            }

            RouteType.Simple ->
            {
                val b = closeAllNoStack()
                if (b && topRouter != this)
                    return topRouter?.route(path, presentation)

                //if last view has tabs try to route to its tab if it has in the root the same path
                val tabRouter = tryRouteToTab(path)
                if (tabRouter != null)
                    return tabRouter

                //if this router in the Closing state pass this route to its path for the execution
                if (isClosing)
                {
                    return if (viewResult == null)
                        parent?.route(path, presentation)
                    else
                        parent?.routeInternal(null, path as RoutePathResult<Any>, RouteType.Simple, presentation, viewResult)
                }

                //if this route has singleTop flag try to find it in the hierarchy and route to the instance
                if (route.singleTop)
                {
                    val exist = scanForPath(path::class)
                    if (exist != null)
                        return closeTo(exist.key)
                }

                //go to route
                return doRoute(route, routeType, path, presentation ?: route.preferredPresentation, viewResult)
            }
        }
    }

    internal open fun tryRouteToTab(path: RoutePath): Router?
    {
        val tabRouter = routerTabsByKey[viewsStack.lastOrNull()?.key]
        if (tabRouter != null)
        {
            val i = tabRouter.containsPath(path)
            if (i != null)
            {
                tabRouter.route(i)
                return tabRouter[i]
            }
        }

        return null
    }

    internal fun testPathUnique(i: Int, path: RoutePath, tabUnique: TabUnique): Boolean
    {
        val first = if (i < viewsStack.size) viewsStack[i] else return false
        val tabPath = pathData[first.key] ?: return false

        return when (tabUnique)
        {
            TabUnique.None -> false
            TabUnique.Class -> tabPath::class == path::class
            TabUnique.Equal -> tabPath == path
        }
    }

    internal fun doDialogRoute(route: RouteControllerInterface<RoutePath, *>, path: RoutePath, viewResult: ViewResultData?): Router?
    {
        val lastIsCompose = viewsStack.lastOrNull()?.route?.isCompose
        val view = if (lastIsCompose != null && lastIsCompose != route.isCompose)
        {
            val newCallerKey = viewsStack.last().key
            val router = createRouter(newCallerKey)
            if (viewResult == null)
                router.route(path = path, presentation = Presentation.Push)
            else
                (router as RouterInternal).routeInternal(null, path as RoutePathResult<Any>, RouteType.Simple, Presentation.Push, viewResult)

            val key = UUID.randomUUID().toString()
            routerManager[key] = router

            route.onCreateView(path).also { it.viewKey = key }
        }
        else
        {
            val view = if (viewResult == null)
                createView(route, route.routeType, path, null)
            else
                createView(route, route.routeType, path, viewResult)

            routerManager.push(view.viewKey, this)
            view
        }

        val command = if (route.routeType == RouteType.Dialog)
            Command.Dialog(view)
        else
            Command.BottomSheet(view)

        commandBuffer.apply(command)
        return routerManager.top
    }

    internal fun doRoute(route: RouteControllerInterface<RoutePath, *>, routeType: RouteType, path: RoutePath, presentation: Presentation, viewResult: ViewResultData?): Router?
    {
        val lastIsCompose = viewsStack.lastOrNull()?.route?.isCompose
        return if (presentation == Presentation.ModalNewTask || (lastIsCompose != null && lastIsCompose != route.isCompose))
        {
            val newCallerKey = viewsStack.last().key
            val router = createRouter(newCallerKey)
            val returnRouter = when (routeType)
            {
                RouteType.Simple ->
                {
                    if (viewResult == null)
                        router.route(path = path, presentation = Presentation.Push)
                    else
                        (router as RouterInternal).routeInternal(null, path as RoutePathResult<Any>, RouteType.Simple, Presentation.Push, viewResult)
                }

                else -> throw ImpossibleRouteException("Modal route can't contains $routeType as ROOT")
            }

            val key = UUID.randomUUID().toString()
            routerManager[key] = router

            if (presentation == Presentation.ModalNewTask)
                commandBuffer.apply(Command.StartModal(key, route.params))
            else
            {
                val view = route.onCreateView(path)
                view.viewKey = key
                commandBuffer.apply(Command.Push(path, view, null))
            }

            returnRouter
        }
        else
        {
            val view = createView(route, routeType, path, viewResult)
            routerManager.push(view.viewKey, this)
            commandBuffer.apply(Command.Push(path, view, route.animationController(presentation, view)))
            this
        }
    }

    internal fun createView(route: RouteControllerInterface<RoutePath, *>, routeType: RouteType, path: RoutePath, viewResult: ViewResultData?): View
    {
        val view = route.onCreateView(path)
        view.viewKey = UUID.randomUUID().toString()
        pathData[view.viewKey] = path
        bindRouter(view.viewKey)

        val chain = scanForChain()

        // if there is a chain and this path is a part of the chain the result has to be delivered to the chain's caller
        if (chain != null && chain.route.isPartOfChain(path::class))
            resultManager.bind(chain.key, view.viewKey)
        else if (viewResult != null) //otherwise check for viewResult and result dispatcher
            bindResult(view.viewKey, viewResult)

        if (viewsStack.isEmpty())
            rootPath = path

        val meta = ViewMeta(view.viewKey, routeType, route.isCompose, route, path::class)
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
        routerManager.remove(v.key)

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
