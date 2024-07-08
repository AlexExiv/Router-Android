package com.speakerboxlite.router

import android.os.Bundle
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.RouteType
import com.speakerboxlite.router.annotations.SingleTop
import com.speakerboxlite.router.annotations.TabUnique
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.command.ViewFactory
import com.speakerboxlite.router.controllers.RouteControllerComposable
import com.speakerboxlite.router.controllers.RouteControllerInterface
import com.speakerboxlite.router.controllers.RouteControllerViewModelHolder
import com.speakerboxlite.router.controllers.RouteControllerViewModelProvider
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.exceptions.ImpossibleRouteException
import com.speakerboxlite.router.exceptions.RouteNotFoundException
import com.speakerboxlite.router.ext.checkMainThread
import com.speakerboxlite.router.ext.getBundles
import com.speakerboxlite.router.ext.putBundles
import com.speakerboxlite.router.result.ResultManager
import com.speakerboxlite.router.result.RouterResultProvider
import com.speakerboxlite.router.result.RouterResultProviderImpl
import com.speakerboxlite.router.result.ViewResultType
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.UUID
import kotlin.reflect.KClass

data class ViewMeta(
    val key: String,
    val routeType: RouteType,
    val presentation: Presentation?,
    val isCompose: Boolean,
    val route: RouteControllerInterface<RoutePath, *>,
    val path: KClass<*>,
    var lockBack: Boolean = false)
{
    override fun toString(): String
    {
        return "(key=$key, routeType=$routeType, route=${route::class.qualifiedName}, path=${path.qualifiedName}, lockBack=$lockBack)"
    }

    fun toBundle(): Bundle =
        Bundle().also {
            it.putString(KEY, key)
            it.putSerializable(ROUTE_TYPE, routeType)
            it.putSerializable(PRESENTATION, presentation)
            it.putBoolean(IS_COMPOSE, isCompose)
            it.putBoolean(LOCK_BACK, lockBack)
        }

    companion object
    {
        const val KEY = "com.speakerboxlite.router.ViewMeta.key"
        const val ROUTE_TYPE = "com.speakerboxlite.router.ViewMeta.routeType"
        const val PRESENTATION = "com.speakerboxlite.router.ViewMeta.presentation"
        const val IS_COMPOSE = "com.speakerboxlite.router.ViewMeta.isCompose"
        const val LOCK_BACK = "com.speakerboxlite.router.ViewMeta.lockBack"

        fun fromBundle(bundle: Bundle, router: RouterSimple): ViewMeta
        {
            val key = bundle.getString(KEY)!!
            val routeType = bundle.getSerializable(ROUTE_TYPE) as RouteType
            val presentation = bundle.getSerializable(PRESENTATION) as Presentation
            val isCompose = bundle.getBoolean(IS_COMPOSE)
            val lockBack = bundle.getBoolean(LOCK_BACK)

            val path = router.getPath(key)!!
            val route = router.findRoute(path)

            return ViewMeta(key, routeType, presentation, isCompose, route, path::class, lockBack)
        }
    }
}

data class ViewResultData(
    val toKey: String,
    val resultType: ViewResultType,
    val result: RouterResultDispatcher<ViewResult, Any>): Serializable
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

open class RouterSimple(
    protected var callerKey: String?,
    parent: RouterSimple?,
    protected val routeManager: RouteManager,
    internal val routerManager: RouterManager,
    protected val resultManager: ResultManager): Router, RouterInternal
{
    enum class State
    {
        READY, CLOSING, CLOSED;

        val isEnd: Boolean get() = this == CLOSING || this == CLOSED
    }

    final override var key: String = UUID.randomUUID().toString()
        private set

    val dataStorage: PathDataStorage = routerManager.provideDataStorage()

    protected val commandBuffer: CommandBuffer = CommandBufferImpl(ViewFactory(this))

    private var weakParent = WeakReference(parent)
    val parent: RouterSimple? get() = weakParent.get()

    private var weakChild = WeakReference<RouterSimple>(null)
    val child: RouterSimple? get() = weakChild.get()

    override val topRouter: Router? get() = routerManager.top
    val branchTopRouter: RouterSimple get() = child?.branchTopRouter ?: this

    override val hasPreviousScreen: Boolean get() = parent != null || _viewsStack.size > 1

    override var lockBack: Boolean
        get() = _viewsStack.lastOrNull()?.lockBack ?: false
        set(value) { _viewsStack.lastOrNull()?.lockBack = value }

    protected val _viewsStack = mutableListOf<ViewMeta>()
    val viewsStack: List<ViewMeta> get() = _viewsStack

    protected val _viewsStackById = mutableMapOf<String, ViewMeta>()
    val viewsStackById: Map<String, ViewMeta> get() = _viewsStackById

    val isCurrentTop: Boolean get() = parent == null && _viewsStack.size == 1

    protected var state = State.READY

    protected val routerTabsByKey = mutableMapOf<String, RouterTabsImpl>()
    internal var rootPath: RoutePath? = null

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

        val viewKey = createView(route, RouteType.Simple, null, path, null)
        routerManager.push(viewKey, this)
        commandBuffer.apply(Command.Replace(path, viewKey))

        return this
    }

    override fun route(path: RouteParamsGen): Router?
    {
        return if (path.execRouter != null && routerManager.getByKey(path.execRouter) !== this)
        {
            routerManager.getByKey(path.execRouter)?.route(path)
        }
        else if (path.isReplace)
        {
            replace(path.path)
        }
        else if (path.tabIndex != null)
        {
            val r = routerTabsByKey[_viewsStack.lastOrNull()?.key]
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
        return if (state.isEnd) parent else this //routerManager.pop()//
    }

    override fun close(): Router?
    {
        val v = _viewsStack.lastOrNull() ?: return (parent ?: this)
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

            if (dataStorage[v.key] != null)
                tryCloseMiddlewares(dataStorage[v.key]!!)

            if (state.isEnd) parent else this
        }

        tryRepeatTopIfEmpty()
        return returnRouter
    }

    protected open fun unbind(key: String)
    {
        resultManager.unbind(key)
        dataStorage[key] = null
        routerTabsByKey[key]?.releaseRouters()
        routerTabsByKey.remove(key)
        routerManager.remove(key)
        unbindRouter(key)
/*
        if (isClosing)
            releaseRouter()*/
    }

    internal fun releaseRouter()
    {
        child?.releaseRouter()
        _viewsStack.toMutableList().forEach { removeView(it.key) }
    }

    override fun closeTo(key: String): Router?
    {
        var toIndex = -1
        var returnRouter: Router? = null
        for (i in _viewsStack.indices)
        {
            if (_viewsStack[i].key == key)
            {
                toIndex = i
                break
            }

            val routerTabs = routerTabsByKey[_viewsStack[i].key]
            if (routerTabs != null && routerTabs.closeTabsTo(key))
            {
                if (i == (_viewsStack.size - 1))
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
        else if (parent == null)
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
        val returnRouter = if (parent == null)
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
        val remove = commandBuffer.sync(_viewsStack.map { it.key })
        remove.forEach { removeView(it, false) }

        if (!state.isEnd && _viewsStack.isEmpty() && rootPath != null)
        {
            RouterConfigGlobal.log(TAG, "|------RESTART ROUTER------|")
            routeInternal(null, rootPath!!, RouteType.Simple, Presentation.Push, null)
        }
    }

    override fun onPrepareView(view: View, viewModel: ViewModel?)
    {
        val path = dataStorage[view.viewKey]!!
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
        val path = dataStorage[view.viewKey]!!
        val route = findRoute(path)
        route as? RouteControllerViewModelProvider<RoutePath, VM> ?: error("${route::class} is not a RouteControllerViewModelProvider")
        return route.onProvideViewModel(modelProvider, path)
    }

    override fun onComposeAnimation(view: View)
    {
        val path = dataStorage[view.viewKey]!!
        val route = findRoute(path)

        //route.animationController()?.onConfigureView(path, view)
    }

    override fun createRouterLocal(key: String): RouterLocal = RouterLocalImpl(key, this)

    override fun createRouterTabs(key: String): RouterTabs = createRouterTabs(key, null, true)

    internal fun createRouterTabs(key: String, tabRouteInParent: Boolean? = null, createReel: Boolean): RouterTabs
    {
        if (routerTabsByKey[key] == null)
        {
            val tabProps = _viewsStackById[key]!!.route.tabProps ?: error("Tab props has not been specified. Use Tab annotation to specify props")
            val _tabRouteInParent = tabRouteInParent ?: tabProps.tabRouteInParent

            routerTabsByKey[key] = RouterTabsImpl(key, _viewsStack.lastOrNull()?.key ?: "", this, tabProps.copy(tabRouteInParent = _tabRouteInParent || !createReel))
            if (createReel)
                routerManager.pushReel(key, routerTabsByKey[key]!!)
        }

        return routerTabsByKey[key]!!
    }

    override fun removeView(key: String)
    {
        removeView(key, true)
    }

    protected fun removeView(key: String, updateState: Boolean)
    {
        RouterConfigGlobal.log(TAG, "Remove view: $key")

        _viewsStack.removeAll { it.key == key }
        _viewsStackById.remove(key)

        if (updateState && _viewsStack.isEmpty() && parent != null)
            state = State.CLOSING

        unbind(key)

        if (updateState && routerManager.getByKey(this.key) == null)
        {
            state = State.CLOSED
            //parent?.weakChild = WeakReference(null) // if reset child it leads to breaking of chain of routers and leaking in case of tabs releasing
        }

        tryRepeatTopIfEmpty()
    }

    internal open fun createRouter(callerKey: String): Router = RouterSimple(callerKey, this, routeManager, routerManager, resultManager)

    internal open fun createRouterTab(callerKey: String, index: Int, tabs: RouterTabsImpl): Router = RouterTabSimple(callerKey, this, routeManager, routerManager, resultManager, index, tabs)

    internal fun findRoute(path: RoutePath): RouteControllerInterface<RoutePath, *> = routeManager.find(path) ?: throw RouteNotFoundException(path)

    internal fun setPath(key: String, path: RoutePath)
    {
        dataStorage[key] = path
    }

    internal fun getPath(key: String): RoutePath? = dataStorage[key]

    internal fun bindRouter(viewKey: String)
    {
        routerManager[viewKey] = this
    }

    internal fun unbindRouter(viewKey: String)
    {
        routerManager[viewKey] = null
    }

    override fun createResultProvider(key: String): RouterResultProvider = RouterResultProviderImpl(key, resultManager)

    override fun performSave(bundle: Bundle)
    {
        bundle.putString(KEY, key)
        bundle.putString(CALLER, callerKey)

        bundle.putSerializable(STATE, state)
        bundle.putSerializable(ROOT_PATH, rootPath)
        bundle.putBundles(VIEW_STACK, _viewsStack.map { it.toBundle() })

        val actualViewStackById = _viewsStack.associateBy { it.key }
        val viewStackByIdBundle = Bundle()
        _viewsStackById.forEach {
            if (actualViewStackById[it.key] == null) // save only items that don't exist in the actual viewStack
                viewStackByIdBundle.putBundle(it.key, it.value.toBundle())
        }
        bundle.putBundle(VIEW_STACK_BY_ID, viewStackByIdBundle)

        val tabsBundle = Bundle()
        routerTabsByKey.forEach {
            val b = Bundle()
            it.value.performSave(b)
            tabsBundle.putBundle(it.key, b)
        }

        bundle.putBundle(ROUTER_TABS, tabsBundle)

        commandBuffer.performSave(bundle)

        if (child != null && child!!.state != State.CLOSED) // don't save child if it has been closed
        {
            val childBundle = Bundle()
            child?.performSave(childBundle)
            bundle.putBundle(CHILD, childBundle)
        }

        if (parent == null)
        {
            val resBundle = Bundle()
            resultManager.performSave(resBundle)
            bundle.putBundle(RESULT_MANAGER, resBundle)
        }
    }

    override fun performRestore(bundle: Bundle)
    {
        key = bundle.getString(KEY)!!
        routerManager.push(this)

        callerKey = bundle.getString(CALLER)
        state = bundle.getSerializable(STATE) as State
        rootPath = bundle.getSerializable(ROOT_PATH) as? RoutePath

        val viewStackBundles = bundle.getBundles(VIEW_STACK)!!
        _viewsStack.clear()
        _viewsStack.addAll(viewStackBundles.map { ViewMeta.fromBundle(it, this) })

        val viewStackByIdBundle = bundle.getBundle(VIEW_STACK_BY_ID)!!
        _viewsStackById.clear()
        viewStackByIdBundle.keySet().forEach {
            _viewsStackById[it] = ViewMeta.fromBundle(viewStackByIdBundle.getBundle(it)!!, this)
        }
        _viewsStackById.putAll(_viewsStack.associateBy { it.key })

        routerTabsByKey.clear()
        val tabsBundle = bundle.getBundle(ROUTER_TABS)!!
        tabsBundle.keySet().forEach {
            if (_viewsStackById[it] != null)
            {
                createRouterTabs(it, null, false)
                routerTabsByKey[it]!!.performRestore(tabsBundle.getBundle(it)!!)
            }
            else
                RouterConfigGlobal.log(TAG, "Unexpected View Key")
        }

        val childBundle = bundle.getBundle(CHILD)
        if (childBundle != null)
        {
            val child = createRouter("") as RouterSimple
            child.performRestore(childBundle)
            weakChild = WeakReference(child)
        }

        commandBuffer.performRestore(bundle)

        if (parent == null)
        {
            val resBundle = bundle.getBundle(RESULT_MANAGER)!!
            resultManager.performRestore(resBundle)
        }
    }

    internal fun bindResult(from: String, result: ViewResultData)
    {
        resultManager.bind(from, result.toKey, result.resultType, result.result)
    }

    internal fun scanForChain(): ViewMeta?
    {
        val chain = _viewsStack.lastOrNull { it.route.isChain }
        if (chain != null)
            return chain

        if (parent != null)
            return parent?.scanForChain()

        return null
    }

    internal open fun scanForPath(path: RoutePath, singleTop: SingleTop, recursive: Boolean = true): ViewMeta?
    {
        for (v in _viewsStack)
        {
            if (arePathsEqual(dataStorage[v.key]!!, path, singleTop))
                return v

            val vs = routerTabsByKey[v.key]?.scanForPath(path, singleTop)
            if (vs != null)
                return v
        }

        if (parent != null && recursive)
            return parent?.scanForPath(path, singleTop)

        return null
    }

    internal fun containsView(key: String): Boolean =
        _viewsStack.lastOrNull { it.key == key } != null

    internal fun tryRouteMiddlewares(params: RouteParamsGen, route: RouteControllerInterface<RoutePath, *>): Boolean
    {
        //onBeforeRoute only available for not empty viewsStack because it refers to current route
        if (_viewsStack.isNotEmpty())
        {
            val curPath = dataStorage[_viewsStack.last().key]!!
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
        if (parent == null || _viewsStack.isNotEmpty())
        {
            if (route.onRoute(this, _viewsStack.lastOrNull()?.let { dataStorage[it.key] }, params))
                return true

            for (mid in route.middlewares)
            {
                if (mid.onRoute(this, _viewsStack.lastOrNull()?.let { dataStorage[it.key] }, params))
                    return true
            }
        }

        return false
    }

    internal fun tryCloseMiddlewares(path: RoutePath)
    {
        val _parent = parent
        val router = if (_viewsStack.isEmpty() && _parent != null) _parent else this
        val prev = if (_viewsStack.isEmpty() && _parent != null)
            _parent._viewsStack.lastOrNull()?.let { _parent.dataStorage[it.key] }
        else
            _viewsStack.lastOrNull()?.let { dataStorage[it.key] }

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
        if (_viewsStack.isEmpty() && parent == null && rootPath != null)
            routeInternal(null, rootPath!!, RouteType.Simple, Presentation.Push, null)
    }

    internal fun _closeTo(i: Int): Router?
    {
        closeAllNoStack()

        if (i == (_viewsStack.size - 1))
            return this

        val deleteCount = _viewsStack.size - i - 1
        for (j in 0 until deleteCount)
            popViewStack()

        commandBuffer.apply(Command.CloseTo(_viewsStack.last().key))

        return this
    }

    internal fun _closeAll(): Router?
    {
        closeAllNoStack()

        val count = _viewsStack.size
        for (i in 0 until count)
            popViewStack()

        commandBuffer.apply(Command.CloseAll)

        return parent ?: this
    }

    internal fun closeAllNoStack(): Boolean
    {
        var was = false
        while (_viewsStack.isNotEmpty() && _viewsStack.last().routeType.isNoStackStructure)
        {
            close()
            was = true
        }

        return was
    }

    override fun routeInternal(execRouter: Router?, path: RoutePath, routeType: RouteType, presentation: Presentation?, viewResult: ViewResultData?): Router?
    {
        RouterConfigGlobal.log(TAG, "Start route with path: ${path::class}")

        checkMainThread("Navigation between screen only possible on the main thread")
        val route = findRoute(path)

        //try to check all middlewares
        val routeParams = RouteParamsGen(execRouter = execRouter?.key, path = path, presentation = presentation, result = viewResult)
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
                if (state.isEnd)
                {
                    return if (viewResult == null)
                        parent?.route(path, presentation)
                    else
                        parent?.routeInternal(null, path as RoutePathResult<Any>, RouteType.Simple, presentation, viewResult)
                }

                //if this route has singleTop flag try to find it in the hierarchy and route to the instance
                if (route.singleTop != SingleTop.None && topRouter != null)
                {
                    val exist = (topRouter as RouterSimple).scanForPath(path, route.singleTop)
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
        val tabsRouter = routerTabsByKey[_viewsStack.lastOrNull()?.key]
        if (tabsRouter != null)
        {
            val i = tabsRouter.containsPath(path)
            if (i != null)
            {
                tabsRouter.route(i)
                tabsRouter[i].closeTabToTop()
                return tabsRouter[i]
            }
        }

        return null
    }

    internal fun testPathUnique(i: Int, path: RoutePath, tabUnique: TabUnique): Boolean
    {
        val first = if (i < _viewsStack.size) _viewsStack[i] else return false
        val tabPath = dataStorage[first.key] ?: return false

        return when (tabUnique)
        {
            TabUnique.None -> false
            TabUnique.Class -> tabPath::class == path::class
            TabUnique.Equal -> tabPath == path
        }
    }

    internal fun doDialogRoute(route: RouteControllerInterface<RoutePath, *>, path: RoutePath, viewResult: ViewResultData?): Router?
    {
        val lastIsCompose = _viewsStack.lastOrNull()?.route?.isCompose
        val view = if (lastIsCompose != null && lastIsCompose != route.isCompose)
        {
            val newCallerKey = _viewsStack.last().key
            val router = createRouter(newCallerKey)
            if (viewResult == null)
                router.route(path = path, presentation = Presentation.Push)
            else
                (router as RouterInternal).routeInternal(null, path as RoutePathResult<Any>, RouteType.Simple, Presentation.Push, viewResult)

            val viewKey = UUID.randomUUID().toString()
            routerManager[viewKey] = router
            setPath(viewKey, path)

            viewKey
        }
        else
        {
            val viewKey = if (viewResult == null)
                createView(route, route.routeType, Presentation.Push, path, null)
            else
                createView(route, route.routeType, Presentation.Push, path, viewResult)

            routerManager.push(viewKey, this)
            viewKey
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
        val lastIsCompose = _viewsStack.lastOrNull()?.route?.isCompose
        return if (presentation == Presentation.ModalNewTask || (lastIsCompose != null && lastIsCompose != route.isCompose))
        {
            val newCallerKey = _viewsStack.last().key
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

            val viewKey = UUID.randomUUID().toString()
            routerManager[viewKey] = router
            weakChild = WeakReference(router as RouterSimple)
            setPath(viewKey, path)

            if (presentation == Presentation.ModalNewTask)
                commandBuffer.apply(Command.StartModal(viewKey, route.params))
            else
                commandBuffer.apply(Command.Push(path, viewKey))

            returnRouter
        }
        else
        {
            val viewKey = createView(route, routeType, presentation, path, viewResult)
            routerManager.push(viewKey, this)
            commandBuffer.apply(Command.Push(path, viewKey))
            this
        }
    }

    internal fun createView(route: RouteControllerInterface<RoutePath, *>, routeType: RouteType, presentation: Presentation?, path: RoutePath, viewResult: ViewResultData?): String
    {
        val viewKey = UUID.randomUUID().toString()
        setPath(viewKey, path)
        bindRouter(viewKey)

        val chain = scanForChain()

        // if there is a chain and this path is a part of the chain the result has to be delivered to the chain's caller
        if (chain != null && chain.route.isPartOfChain(path::class))
            resultManager.bind(chain.key, viewKey)
        else if (viewResult != null) //otherwise check for viewResult and result dispatcher
            bindResult(viewKey, viewResult)

        if (_viewsStack.isEmpty())
            rootPath = path

        val meta = ViewMeta(viewKey, routeType, presentation, route.isCompose, route, path::class)
        _viewsStack.add(meta)
        _viewsStackById[meta.key] = meta

        return viewKey
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
        if (_viewsStack.isEmpty())
            return null

        val v = _viewsStack.removeLast()
        routerManager.remove(v.key)

        if (_viewsStack.isEmpty() && parent != null)
            state = State.CLOSING

        return v
    }

    internal fun buildViewStackPath(): List<ViewMeta>
    {
        var prev: RouterSimple? = this
        val totalStack = mutableListOf<ViewMeta>()
        while (prev != null)
        {
            totalStack.addAll(0, prev._viewsStack)
            prev = prev.parent
        }

        return totalStack
    }

    companion object
    {
        val TAG = "RouterSimple"

        val KEY = "com.speakerboxlite.router.RouterSimple.key"
        val CALLER = "com.speakerboxlite.router.RouterSimple.callerKey"
        val STATE = "com.speakerboxlite.router.RouterSimple.state"
        val ROOT_PATH = "com.speakerboxlite.router.RouterSimple.rootPathKey"
        val VIEW_STACK = "com.speakerboxlite.router.RouterSimple.viewStack"
        val VIEW_STACK_BY_ID = "com.speakerboxlite.router.RouterSimple.viewStackById"
        val ROUTER_TABS = "com.speakerboxlite.router.RouterSimple.routerTabsByKey"
        val CHILD = "com.speakerboxlite.router.RouterSimple.child"
        val RESULT_MANAGER = "com.speakerboxlite.router.RouterSimple.resultManager"

        fun arePathsEqual(left: RoutePath, right: RoutePath, singleTop: SingleTop): Boolean =
            when (singleTop)
            {
                SingleTop.None -> false
                SingleTop.Class -> left::class == right::class
                SingleTop.Equal -> left == right
            }
    }
}
