package com.speakerboxlite.router

import android.os.Bundle
import com.speakerboxlite.router.controllers.RouteControllerComponent
import com.speakerboxlite.router.controllers.RouteControllerInterface
import com.speakerboxlite.router.controllers.RouteControllerViewModelHolderComponent
import com.speakerboxlite.router.exceptions.RouteNotFoundException
import com.speakerboxlite.router.result.ResultManager
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

open class RouterInjector(
    callerKey: String?,
    parent: RouterSimple?,
    routeManager: RouteManager,
    routerManager: RouterManager,
    resultManager: ResultManager,
    protected val componentProvider: ComponentProvider): RouterSimple(callerKey, parent, routeManager, routerManager, resultManager)
{
    data class ViewMetaComponent(
        val componentKey: String,
        val routeComponent: RouteControllerComponent<RoutePath, *, *>,
        val componentPathData: RoutePath)
    {
        fun toBundle(): Bundle =
            Bundle()
                .also {
                    it.putString(COMP_KEY, componentKey)
                    it.putSerializable(PATH, componentPathData)
                }

        companion object
        {
            const val COMP_KEY = "com.speakerboxlite.router.RouterInjector.ViewMetaComponent.componentKey"
            const val PATH = "com.speakerboxlite.router.RouterInjector.ViewMetaComponent.componentPathData"

            fun fromBundle(bundle: Bundle, router: RouterInjector): ViewMetaComponent
            {
                val key = bundle.getString(COMP_KEY)!!
                val path = bundle.getSerializable(PATH) as RoutePath
                val cntrl = router.findRoute(path) as RouteControllerComponent<RoutePath, *, *>

                return ViewMetaComponent(key, cntrl, path)
            }
        }
    }

    val parentInjector: RouterInjector get() = parent as RouterInjector

    protected val appComponentClass: KClass<*> by lazy {
        componentProvider.appComponent::class.superclasses[0]
    }

    protected val metaComponents = mutableMapOf<String, ViewMetaComponent>()

    override fun onPrepareView(view: View, viewModel: ViewModel?)
    {
        super.onPrepareView(view, viewModel)

        val path = dataStorage[view.viewKey]!!
        val route = routeManager.find(path) ?: throw RouteNotFoundException(path)
        val routeComponent = route as? RouteControllerComponent<RoutePath, View, *>

        if (routeComponent != null)
        {
            val component = onComposeInjector(view.viewKey, route)
            routeComponent.onPrepareView(this, view, path, component)

            if (viewModel != null)
            {
                (route as? RouteControllerViewModelHolderComponent<ViewModel>)?.onPrepareViewModel(this, view.viewKey, viewModel, component)
            }
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

    override fun performSave(bundle: Bundle)
    {
        super.performSave(bundle)

        val metaBundle = Bundle()
        metaComponents.forEach { metaBundle.putBundle(it.key, it.value.toBundle()) }
        bundle.putBundle(META_COMPONENTS, metaBundle)
    }

    override fun performRestore(bundle: Bundle)
    {
        super.performRestore(bundle)

        val metaBundle = bundle.getBundle(META_COMPONENTS)!!
        metaComponents.clear()
        metaBundle.keySet().forEach {
            metaComponents[it] = ViewMetaComponent.fromBundle(metaBundle.getBundle(it)!!, this)
        }
    }

    internal fun connectComponent(parentKey: String, childKey: String)
    {
        componentProvider.connectComponent(parentKey, childKey)
    }

    protected fun onComposeInjector(viewKey: String, route: RouteControllerComponent<RoutePath, *, *>): Any
    {
        val compClass = route.componentClass
        if (compClass == appComponentClass)
            return componentProvider.appComponent

        val compKey = componentProvider.componentKey(viewKey)
        val meta = _viewsStackById[compKey]!!

        return if (metaComponents[meta.key] != null)
        {
            val mc = metaComponents[meta.key]!!
            var comp = componentProvider.find(mc.componentKey)
            if (comp == null)
                comp = mc.routeComponent.onCreateInjector(mc.componentPathData, componentProvider.appComponent)
                    .also { componentProvider.bind(mc.componentKey, it) }

            comp
        }
        else
        {
            scanForTopComponent(meta.key, metaComponents, compClass)
        }
    }

    protected fun scanForTopComponent(viewKey: String, metaComponents: MutableMap<String, ViewMetaComponent>, compClass: KClass<*>): Any
    {
        if (parent == null && _viewsStack.size == 1)
            return getComponent(viewKey, metaComponents, _viewsStack[0].key, _viewsStack[0].route)

        val path = routerManager.buildPathToRoot()
        var startIndex = path.indexOfFirst { it.viewKey == viewKey }
        if (startIndex == -1) // we suppose in this case that this screen will be in top of the stack
            startIndex = 0
        //assert(startIndex != -1) { "Couldn't find view with id = $viewKey in the stack" }

        for (i in startIndex until path.size)
        {
            val router = path[i].router as RouterSimple
            val v = router.viewsStackById[path[i].viewKey]!!
            val routeComp = (v.route as? RouteControllerComponent<RoutePath, View, *>)?.componentClass ?: continue
            if (routeComp == compClass && v.route.creatingInjector)
                return getComponent(viewKey, metaComponents, v.key, v.route)
        }

        throw RuntimeException("Couldn't find appropriate method to create component: ${compClass}. Maybe your forgot override onCreateInjector method?")
    }

    protected fun getComponent(viewKey: String, metaComponents: MutableMap<String, ViewMetaComponent>, componentKey: String, route: RouteControllerInterface<RoutePath, *>): Any
    {
        var comp = componentProvider.find(componentKey)
        val routeComponent = route as? RouteControllerComponent<RoutePath, *, *> ?: throw RuntimeException("")

        if (comp == null)
        {
            comp = routeComponent.onCreateInjector(dataStorage[componentKey]!!, componentProvider.appComponent)
                .also { componentProvider.bind(componentKey, it) }
        }

        metaComponents[viewKey] = ViewMetaComponent(componentKey, routeComponent, dataStorage[componentKey]!!)

        return comp
    }

    companion object
    {
        const val META_COMPONENTS = "com.speakerboxlite.router.RouterInjector.metaComponents"
    }
}