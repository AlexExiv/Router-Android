package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.ViewVM

abstract class RouteControllerVM<Path: RoutePath, VM: ViewModel, V: ViewVM<VM>>: RouteController<Path, V>(),
    RouteControllerComposable<Path, V>
{
    override fun onComposeView(router: Router, view: V, path: Path)
    {
        val vm = onCreateViewModel(view, path)
        view.viewModel = vm

        if (!view.viewModel.isInit)
        {
            view.viewModel.router = router
            view.viewModel.resultProvider = router.createResultProvider(view.viewKey)
                .also { it.start() }
        }
    }

    abstract protected fun onCreateViewModel(view: V, path: Path): VM
}

