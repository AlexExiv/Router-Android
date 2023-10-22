package com.speakerboxlite.router

import com.speakerboxlite.router.controllers.RouteControllerComponent
import com.speakerboxlite.router.exceptions.RouteNotFoundException
import com.speakerboxlite.router.ext.retrieveComponent
import com.speakerboxlite.router.result.ResultManager
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

open class RouterInjector(callerKey: String?,
                          parent: RouterSimple?,
                          routeManager: RouteManager,
                          routerManager: RouterManager,
                          resultManager: ResultManager,
                          protected val componentProvider: ComponentProvider): RouterSimple(callerKey, parent, routeManager, routerManager, resultManager)
{
    val parentInjector: RouterInjector get() = parent as RouterInjector

    protected val appComponentClass: KClass<*> by lazy {
        componentProvider.appComponent::class.superclasses[0]
    }

    override fun onComposeView(view: View)
    {
        super.onComposeView(view)

        val path = pathData[view.viewKey]!!
        val route = routeManager.find(path) ?: throw RouteNotFoundException(path)

        (route as? RouteControllerComponent<RoutePath, View, *>)?.also {
            val compKey = componentProvider.componentKey(view.viewKey)
            val i = viewsStack.indexOfFirst { it.key == compKey }
            val compClass = route::class.retrieveComponent() ?: throw RuntimeException("Couldn't retrieve Component class")
            val comp = scanForTopComponent(i, compClass)
            it.onComposeView(this, view, path, comp)
        }
    }

    override fun unbind(key: String)
    {
        super.unbind(key)
        componentProvider.unbind(key)
    }

    override fun createRouter(callerKey: String): Router = RouterInjector(callerKey, this, routeManager, routerManager, resultManager, componentProvider)

    override fun createRouterTab(callerKey: String, index: Int, tabs: RouterTabsImpl): Router = RouterTabInjector(callerKey, this, routeManager, routerManager, resultManager, componentProvider, index, tabs)

    internal fun connectComponent(parentKey: String, childKey: String)
    {
        componentProvider.connectComponent(parentKey, childKey)
    }

    protected fun scanForTopComponent(startFrom: Int?, compClass: KClass<*>): Any
    {
        if (compClass == appComponentClass)
            return componentProvider.appComponent

        if (parent == null && viewsStack.size == 1)
        {
            var comp = componentProvider.find(viewsStack[0].key)
            if (comp == null)
            {
                val routeComponent = viewsStack[0].route as? RouteControllerComponent<RoutePath, *, *> ?: throw RuntimeException("")
                comp = routeComponent.onCreateInjector(pathData[viewsStack[0].key]!!, componentProvider.appComponent)
                componentProvider.bind(viewsStack[0].key, comp)
            }

            return comp
        }

        val s = startFrom ?: (viewsStack.size - 1)
        for (i in s downTo 0)
        {
            val v = viewsStack[i]
            val routeComp = v.route::class.retrieveComponent()
            if (routeComp == compClass && v.route.creatingInjector)
            {
                var comp = componentProvider.find(v.key)
                if (comp == null)
                {
                    val routeComponent = v.route as? RouteControllerComponent<RoutePath, *, *> ?: throw RuntimeException("")
                    comp = routeComponent.onCreateInjector(pathData[v.key]!!, componentProvider.appComponent)
                    componentProvider.bind(v.key, comp)
                }

                return comp
            }
        }

        if (parent != null)
            return parentInjector.scanForTopComponent(null, compClass)

        throw RuntimeException("Couldn't find appropriate method to create component: ${compClass}. Maybe your forgot override onCreateInjector method?")
    }
}