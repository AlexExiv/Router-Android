package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterModelProvider
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.ViewVM

abstract class RouteControllerVM<Path: RoutePath, VM: ViewModel, ModelProvider: RouterModelProvider, V>: RouteController<Path, V>(),
    RouteControllerComposable<Path, V>,
    RouteControllerViewModelProvider<Path, VM> where V: View, V: ViewVM<VM>
{
    override fun onPrepareView(router: Router, view: V, path: Path)
    {
        if (!view.viewModel.isInit)
        {
            view.viewModel.router = router
            view.viewModel.resultProvider = router.createResultProvider(view.viewKey)
                .also { it.start() }

            view.viewModel.onInit()
            view.viewModel.isInit = true
        }
    }

    override fun onProvideViewModel(modelProvider: RouterModelProvider, path: Path): VM
    {
        return onCreateViewModel(modelProvider as ModelProvider, path)
    }

    abstract protected fun onCreateViewModel(modelProvider: ModelProvider, path: Path): VM
}

