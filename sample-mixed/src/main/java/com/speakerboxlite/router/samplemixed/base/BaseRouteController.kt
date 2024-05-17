package com.speakerboxlite.router.samplemixed.base

import com.speakerboxlite.router.compose.AndroidComposeViewModelProvider
import com.speakerboxlite.router.controllers.RouteControllerVMC
import com.speakerboxlite.router.fragment.FragmentViewModelProvider
import com.speakerboxlite.router.samplemixed.di.AppComponent

typealias RouteControllerComposeApp<Path, VM, V> = RouteControllerVMC<Path, VM, AndroidComposeViewModelProvider, V, AppComponent>

typealias RouteControllerFragmentApp<Path, VM, V> = RouteControllerVMC<Path, VM, FragmentViewModelProvider, V, AppComponent>
