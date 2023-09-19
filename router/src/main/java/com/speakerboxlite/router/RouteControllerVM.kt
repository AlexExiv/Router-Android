package com.speakerboxlite.router

abstract class RouteControllerVM<Path: RoutePath, VM: ViewModel, V: ViewVM<VM>>: RouteControllerBase<Path, V>(),
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

abstract class RouteControllerVMC<Path: RoutePath, VM: ViewModel, V: ViewVM<VM>, Component>: RouteControllerBase<Path, V>(),
        RouteControllerComponent<Path, V>
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

        onInject(view, vm, component as Component)
    }

    abstract protected fun onCreateViewModel(view: V, path: Path): VM

    override fun onCreateInjector(path: Path, component: Any): Any = component

    abstract protected fun onInject(view: V, vm: VM, component: Component)
}