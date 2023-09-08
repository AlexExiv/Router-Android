package com.speakerboxlite.router.sample.base

import com.speakerboxlite.router.RouteControllerBase
import com.speakerboxlite.router.sample.di.AppComponent

typealias RouteControllerApp<Path, VM, V> = RouteControllerBase<Path, VM, V, AppComponent>
