package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterModelProvider
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewModel
import kotlin.reflect.KClass

abstract class RouteControllerVMC<Path: RoutePath, VM: ViewModel, ModelProvider: RouterModelProvider, V, C: Component>:
    RouteController<Path, V>(),
    RouteControllerComponent<Path, V, C>,
    RouteControllerViewModelProvider<Path, VM>,
    RouteControllerViewModelHolderComponent<VM> where V: View
{
    final override lateinit var componentClass: KClass<C>

    override fun onPrepareView(router: Router, view: V, path: Path, component: Any)
    {
        onInject(view, component as C)
    }

    override fun onProvideViewModel(modelProvider: RouterModelProvider, path: Path): VM
    {
        return onCreateViewModel(modelProvider as ModelProvider, path)
    }

    override fun onPrepareViewModel(router: Router, key: String, vm: VM, component: Any)
    {
        if (!vm.isInit)
        {
            vm.router = router
            vm.resultProvider = router.createResultProvider(key)
                .also { it.start() }
        }

        onInject(vm, component as C)

        if (!vm.isInit)
        {
            vm.onInit()
            vm.isInit = true
        }
    }

    protected abstract fun onCreateViewModel(modelProvider: ModelProvider, path: Path): VM

    override fun onCreateInjector(path: Path, component: Any): Any = component

    protected abstract fun onInject(vm: VM, component: C)

    protected open fun onInject(view: V, component: C) {}
}