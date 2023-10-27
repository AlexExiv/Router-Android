package com.speakerboxlite.router

import com.speakerboxlite.router.controllers.RouteControllerComponent
import com.speakerboxlite.router.controllers.RouteControllerInterface
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
    data class ViewMetaComponent(val componentKey: String,
                                 val routeComponent: RouteControllerComponent<RoutePath, *, *>,
                                 val componentPathData: RoutePath)

    val parentInjector: RouterInjector get() = parent as RouterInjector

    protected val appComponentClass: KClass<*> by lazy {
        componentProvider.appComponent::class.superclasses[0]
    }

    protected val metaComponents = mutableMapOf<String, ViewMetaComponent>()

    override fun onComposeView(view: View)
    {
        super.onComposeView(view)

        val path = pathData[view.viewKey]!!
        val route = routeManager.find(path) ?: throw RouteNotFoundException(path)
        val routeComponent = route as? RouteControllerComponent<RoutePath, View, *>

        if (routeComponent != null)
        {
            val component = onComposeInjector(view.viewKey, route)
            routeComponent.onComposeView(this, view, path, component)
        }
    }

    override fun unbind(key: String)
    {
        super.unbind(key)

        componentProvider.unbind(key)
        metaComponents.remove(key)
    }

    override fun createRouter(callerKey: String): Router = RouterInjector(callerKey, this, routeManager, routerManager, resultManager, componentProvider)

    override fun createRouterTab(callerKey: String, index: Int, tabs: RouterTabsImpl): Router = RouterTabInjector(callerKey, this, routeManager, routerManager, resultManager, componentProvider, index, tabs)

    internal fun connectComponent(parentKey: String, childKey: String)
    {
        componentProvider.connectComponent(parentKey, childKey)
    }

    protected fun onComposeInjector(viewKey: String, route: RouteControllerInterface<RoutePath, *>): Any
    {
        val compKey = componentProvider.componentKey(viewKey)
        val meta = viewsStackById[compKey]!!

        return if (metaComponents[meta.key] != null)
        {
            val mc = metaComponents[meta.key]!!
            var comp = componentProvider.find(mc.componentKey)
            if (comp == null)
                comp = mc.routeComponent.onCreateInjector(mc.componentPathData, componentProvider.appComponent)

            comp
        }
        else
        {
            val i = viewsStack.indexOfFirst { it.key == compKey }
            val compClass = route::class.retrieveComponent() ?: throw RuntimeException("Couldn't retrieve Component class")
            val comp = scanForTopComponent(meta.key, metaComponents, i, compClass)
            comp
        }
    }

    protected fun scanForTopComponent(viewKey: String, metaComponents: MutableMap<String, ViewMetaComponent>, startFrom: Int?, compClass: KClass<*>): Any
    {
        if (compClass == appComponentClass)
            return componentProvider.appComponent

        if (startFrom == -1)
            throw IllegalStateException("There is no such record in the views stack. How could it be.")

        if (parent == null && viewsStack.size == 1)
            return getComponent(viewKey, metaComponents, viewsStack[0].key, viewsStack[0].route)

        val s = startFrom ?: (viewsStack.size - 1)
        for (i in s downTo 0)
        {
            val v = viewsStack[i]
            val routeComp = v.route::class.retrieveComponent()
            if (routeComp == compClass && v.route.creatingInjector)
                return getComponent(viewKey, metaComponents, v.key, v.route)
        }

        if (parent != null)
            return parentInjector.scanForTopComponent(viewKey, metaComponents, null, compClass)

        throw RuntimeException("Couldn't find appropriate method to create component: ${compClass}. Maybe your forgot override onCreateInjector method?")
    }

    protected fun getComponent(viewKey: String, metaComponents: MutableMap<String, ViewMetaComponent>, componentKey: String, route: RouteControllerInterface<RoutePath, *>): Any
    {
        var comp = componentProvider.find(componentKey)
        val routeComponent = route as? RouteControllerComponent<RoutePath, *, *> ?: throw RuntimeException("")

        if (comp == null)
        {
            comp = routeComponent.onCreateInjector(pathData[componentKey]!!, componentProvider.appComponent)
            componentProvider.bind(componentKey, comp)
        }

        metaComponents[viewKey] = ViewMetaComponent(componentKey, routeComponent, pathData[componentKey]!!)

        return comp
    }
}