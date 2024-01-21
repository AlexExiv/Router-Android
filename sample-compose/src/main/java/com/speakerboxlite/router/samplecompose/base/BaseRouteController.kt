package com.speakerboxlite.router.samplecompose.base

import com.speakerboxlite.router.compose.AndroidComposeViewModelProvider
import com.speakerboxlite.router.controllers.RouteControllerVMC
import com.speakerboxlite.router.samplecompose.di.AppComponent

typealias RouteControllerApp<Path, VM, V> = RouteControllerVMC<Path, VM, AndroidComposeViewModelProvider, V, AppComponent>
