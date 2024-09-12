package com.speakerboxlite.router.sample.shared

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteControllerVMC
import com.speakerboxlite.router.fragment.FragmentViewModelProvider
import com.speakerboxlite.router.sample.di.AppComponent

typealias RouteControllerShared<P, VM, V> = RouteControllerVMC<P, VM, FragmentViewModelProvider, V, SharedComponent>

class SharedPath: RoutePath

@Route
abstract class SharedRouteController: RouteControllerShared<SharedPath, SharedViewModel, SharedFragment>()
{
    override fun onCreateInjector(path: SharedPath, component: Any): Any =
        DaggerSharedComponent.builder()
            .appComponent(component as AppComponent)
            .sharedModule(SharedModule(SharedData()))
            .build()

    override fun onClearInjector(component: Any)
    {
        (component as SharedComponent).provideSharedData().onDispose()
    }
}
