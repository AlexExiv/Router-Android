package com.speakerboxlite.router.sample.simple.component

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteControllerC
import com.speakerboxlite.router.sample.di.AppComponent

class SimpleComponentPath: RoutePath

@Route
abstract class SimpleComponentRouteController: RouteControllerC<SimpleComponentPath, SimpleComponentFragment, AppComponent>()
