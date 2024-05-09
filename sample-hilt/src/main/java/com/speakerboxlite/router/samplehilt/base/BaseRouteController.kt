package com.speakerboxlite.router.samplehilt.base

import com.speakerboxlite.router.androidhilt.AndroidHiltViewModelProvider
import com.speakerboxlite.router.controllers.RouteControllerVM

typealias RouteControllerApp<Path, VM, V> = RouteControllerVM<Path, VM, AndroidHiltViewModelProvider, V>
