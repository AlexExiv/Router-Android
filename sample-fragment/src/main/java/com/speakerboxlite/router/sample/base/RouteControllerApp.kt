package com.speakerboxlite.router.sample.base

import com.speakerboxlite.router.controllers.RouteControllerVMC
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.sample.di.AppComponent

typealias RouteControllerApp<Path, VM, V> = RouteControllerVMC<Path, VM, AndroidViewModelProvider, V, AppComponent>

enum class RouteStyle
{
    Default, Landscape
}
