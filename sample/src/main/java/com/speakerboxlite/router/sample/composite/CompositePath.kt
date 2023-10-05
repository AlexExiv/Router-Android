package com.speakerboxlite.router.sample.composite

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.base.RouteControllerVMCBase
import com.speakerboxlite.router.sample.di.AppComponent

typealias RouteControllerComposite<P, VM, V> = RouteControllerVMCBase<P, VM, V, CompositeComponent>

class CompositePath: RoutePath

@Route
abstract class CompositeRouteController: RouteControllerComposite<CompositePath, CompositeViewModel, CompositeFragment>()
{
    override fun onCreateInjector(path: CompositePath, component: Any): Any =
        DaggerCompositeComponent.builder()
            .appComponent(component as AppComponent)
            .compositeModule(CompositeModule(CompositeData()))
            .build()
}
