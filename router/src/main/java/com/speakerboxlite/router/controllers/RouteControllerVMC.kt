package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterModelProvider
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.ViewVM
import kotlin.reflect.KClass

abstract class RouteControllerVMC<Path: RoutePath, VM: ViewModel, ModelProvider: RouterModelProvider, V, C: Component>: RouteController<Path, V>(),
    RouteControllerComponent<Path, V, C>,
    RouteControllerViewModelProvider<Path, VM> where V: View, V: ViewVM<VM>
{
    final override lateinit var componentClass: KClass<C>

    override fun onPrepareView(router: Router, view: V, path: Path, component: Any)
    {
        if (!view.viewModel.isInit)
        {
            view.viewModel.router = router
            view.viewModel.resultProvider = router.createResultProvider(view.viewKey)
                .also { it.start() }
        }

        onInject(view, view.viewModel, component as C)

        if (!view.viewModel.isInit)
        {
            view.viewModel.onInit()
            view.viewModel.isInit = true
        }
    }

    override fun onProvideViewModel(modelProvider: RouterModelProvider, path: Path): VM
    {
        return onCreateViewModel(modelProvider as ModelProvider, path)
    }

    abstract protected fun onCreateViewModel(modelProvider: ModelProvider, path: Path): VM

    override fun onCreateInjector(path: Path, component: Any): Any = component

    abstract protected fun onInject(view: V, vm: VM, component: C)
}