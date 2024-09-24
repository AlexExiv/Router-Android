package com.speakerboxlite.router.samplemixed.mixed

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.compose.AndroidComposeViewModelProvider
import com.speakerboxlite.router.controllers.RouteControllerVMC
import com.speakerboxlite.router.fragment.FragmentViewModelProvider
import com.speakerboxlite.router.samplemixed.di.AppComponent

typealias RouteControllerComposeMixed<P, VM, V> = RouteControllerVMC<P, VM, AndroidComposeViewModelProvider, V, MixedComponent>
typealias RouteControllerFragmentMixed<P, VM, V> = RouteControllerVMC<P, VM, FragmentViewModelProvider, V, MixedComponent>

class MixedComposePath: RoutePath

@Route
abstract class MixedComposeRouteController: RouteControllerComposeMixed<MixedComposePath, MixedComposeViewModel, MixedComposeView>()
{
    override fun onCreateInjector(path: MixedComposePath, component: Any): Any =
        DaggerMixedComponent.builder()
            .appComponent(component as AppComponent)
            .mixedModule(MixedModule(MixedData()))
            .build()

    override fun onClearInjector(component: Any)
    {
        (component as MixedComponent).provideMixedData().onDispose()
    }
}