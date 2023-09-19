package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.ViewVM

abstract class RouteControllerVMC<Path: RoutePath, VM: ViewModel, V: ViewVM<VM>, C: Component>: RouteController<Path, V>(),
    RouteControllerComponent<Path, V, C>
{
    override fun onComposeView(router: Router, view: V, path: Path, component: Any)
    {
        val vm = onCreateViewModel(view, path)
        view.viewModel = vm

        if (!view.viewModel.isInit)
        {
            view.viewModel.router = router
            view.viewModel.resultProvider = router.createResultProvider(view.viewKey)
                .also { it.start() }
        }

        onInject(view, vm, component as C)
    }

    abstract protected fun onCreateViewModel(view: V, path: Path): VM

    override fun onCreateInjector(path: Path, component: Any): Any = component

    abstract protected fun onInject(view: V, vm: VM, component: C)
}