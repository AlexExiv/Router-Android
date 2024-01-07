package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterModelProvider
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewModel

abstract class RouteControllerVM<Path: RoutePath, VM: ViewModel, ModelProvider: RouterModelProvider, V>:
    RouteController<Path, V>(),
    RouteControllerComposable<Path, V>,
    RouteControllerViewModelProvider<Path, VM>,
    RouteControllerViewModelHolder<VM> where V: View
{
    val viewModelInit = mutableMapOf<String, Boolean>()

    override fun onPrepareView(router: Router, view: V, path: Path)
    {

    }

    override fun onProvideViewModel(modelProvider: RouterModelProvider, path: Path): VM
    {
        return onCreateViewModel(modelProvider as ModelProvider, path)
    }

    override fun onPrepareViewModel(router: Router, key: String, vm: VM)
    {
        if (viewModelInit[key] != true)
        {
            vm.router = router
            vm.resultProvider = router.createResultProvider(key)
                .also { it.start() }

            vm.onInit()
            viewModelInit[key] = true
        }
    }

    abstract protected fun onCreateViewModel(modelProvider: ModelProvider, path: Path): VM
}

