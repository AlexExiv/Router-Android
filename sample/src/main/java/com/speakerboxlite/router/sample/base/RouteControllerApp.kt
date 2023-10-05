package com.speakerboxlite.router.sample.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.ViewVM
import com.speakerboxlite.router.controllers.AnimationController
import com.speakerboxlite.router.controllers.Component
import com.speakerboxlite.router.controllers.RouteControllerVMC
import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.di.AppComponent

abstract class RouteControllerVMCBase<Path: RoutePath, VM: ViewModel, V: ViewVM<VM>, C: Component>:
    RouteControllerVMC<Path, VM, V, C>(), AnimationController<Path, V>
{
    override fun onConfigureAnimation(path: Path, transaction: FragmentTransaction, current: Fragment?, next: V, replacing: Boolean)
    {
        transaction.setCustomAnimations(R.anim.def_fragment_in, R.anim.def_fragment_exit, R.anim.def_fragment_pop_enter, R.anim.def_fragment_out)
    }
}

typealias RouteControllerApp<Path, VM, V> = RouteControllerVMCBase<Path, VM, V, AppComponent>

enum class RouteStyle
{
    Default, Landscape
}
