package com.speakerboxlite.router

import android.os.Bundle
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.SingleTop
import com.speakerboxlite.router.annotations.TabUnique
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.command.ViewFactory
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.controllers.TabsProperties
import java.lang.ref.WeakReference
import java.util.UUID
import kotlin.reflect.KClass

class RouterTabsImpl(
    var viewKey: String,
    val callerKey: String,
    router: RouterSimple,
    tabRouteInParent: Boolean,
    backToFirst: Boolean,
    tabUnique: TabUnique): RouterTabs
{
    constructor(viewKey: String, callerKey: String, router: RouterSimple, tabsProperties: TabsProperties):
            this(viewKey, callerKey, router, tabsProperties.tabRouteInParent, tabsProperties.backToFirst, tabsProperties.tabUnique)

    var tabRouteInParent: Boolean = tabRouteInParent
        private set
    var backToFirst: Boolean = backToFirst
        private set
    var tabUnique: TabUnique = tabUnique
        private set

    protected val weakRouter = WeakReference(router)
    protected val router: RouterSimple get() = weakRouter.get()!!

    override var tabChangeCallback: OnTabChangeCallback? = null

    protected val commandBuffer: CommandBuffer = CommandBufferImpl(null)
    protected val tabRouters = mutableMapOf<Int, RouterSimple>()
    protected val tabRoutersKeys = mutableMapOf<Int, String>()

    override var tabIndex = 0

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
            tabRoutersKeys[index] = viewKey
            router.routerManager[viewKey] = tabRouters[index]!!

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
        //releaseRouters()
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
        //releaseRouters()
        return router.closeToTop()
    }

    override operator fun get(index: Int): RouterTab = tabRouters[index]!! as RouterTab

    internal fun scanForPath(path: RoutePath, singleTop: SingleTop): ViewMeta?
    {
        for (kv in tabRouters)
        {
            val top = kv.value.branchTopRouter
            val v = top.scanForPath(path, singleTop, false)
            if (v != null)
            {
                route(kv.key)
                top.closeTo(v.key)
                return v
            }
        }

        return null
    }

    internal fun releaseRouters()
    {
        tabRoutersKeys.forEach {
            tabRouters[it.key]!!.releaseRouter()
            router.routerManager[it.value] = null
        }
    }

    internal fun containsPath(path: RoutePath): Int?
    {
        val i = tabRouters.values.indexOfFirst { it.testPathUnique(0, path, tabUnique) }
        return if (i == -1) null else i
    }

    internal fun performSave(bundle: Bundle)
    {
        bundle.putString(KEY, viewKey)
        bundle.putString(CALLER, callerKey)
        bundle.putBoolean(TAB_ROUTE_IN_PARENT, tabRouteInParent)
        bundle.putBoolean(BACK_TO_FIRST, backToFirst)
        bundle.putSerializable(TAB_UNIQUE, tabUnique)
        bundle.putInt(TAB_INDEX, tabIndex)

        val tabRoutersKeysBundle = Bundle()
        tabRoutersKeys.forEach {
            tabRoutersKeysBundle.putString(it.key.toString(), it.value)
        }
        bundle.putBundle(TAB_ROUTERS_KEY, tabRoutersKeysBundle)

        val tabRoutersBundle = Bundle()
        tabRouters.forEach {
            val b = Bundle()
            it.value.performSave(b)
            tabRoutersBundle.putBundle(it.key.toString(), b)
        }
        bundle.putBundle(TAB_ROUTERS, tabRoutersBundle)
    }

    internal fun performRestore(bundle: Bundle)
    {
        viewKey = bundle.getString(KEY)!!
        tabRouteInParent = bundle.getBoolean(TAB_ROUTE_IN_PARENT)
        backToFirst = bundle.getBoolean(BACK_TO_FIRST)
        tabUnique = bundle.getSerializable(TAB_UNIQUE) as TabUnique
        tabIndex = bundle.getInt(TAB_INDEX) // Will it be restored properly?

        tabRouters.clear()
        val tabRoutersBundle = bundle.getBundle(TAB_ROUTERS)!!
        tabRoutersBundle.keySet().forEach {
            val index = it.toInt()
            val routerTab = router.createRouterTab(callerKey, index, this)
            routerTab.performRestore(tabRoutersBundle.getBundle(it)!!)

            tabRouters[index] = routerTab as RouterSimple
        }

        tabRoutersKeys.clear()
        val tabRoutersKeysBundle = bundle.getBundle(TAB_ROUTERS_KEY)!!
        tabRoutersKeysBundle.keySet().forEach {
            val index = it.toInt()
            val viewKey = tabRoutersKeysBundle.getString(it)!!

            tabRoutersKeys[index] = viewKey
            router.routerManager[viewKey] = tabRouters[index]!!
        }
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

    companion object
    {
        const val KEY = "com.speakerboxlite.router.RouterTabsImpl.viewKey"
        const val CALLER = "com.speakerboxlite.router.RouterTabsImpl.callerKey"
        const val TAB_ROUTE_IN_PARENT = "com.speakerboxlite.router.RouterTabsImpl.tabRouteInParent"
        const val BACK_TO_FIRST = "com.speakerboxlite.router.RouterTabsImpl.backToFirst"
        const val TAB_UNIQUE = "com.speakerboxlite.router.RouterTabsImpl.tabUnique"
        const val TAB_INDEX = "com.speakerboxlite.router.RouterTabsImpl.tabIndex"
        const val TAB_ROUTERS_KEY = "com.speakerboxlite.router.RouterTabsImpl.tabRoutersKeys"
        const val TAB_ROUTERS = "com.speakerboxlite.router.RouterTabsImpl.tabRouters"
    }
}