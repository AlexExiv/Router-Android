package com.speakerboxlite.router.sample.compose

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

class MainComposePath: RoutePath

@Route
abstract class SimpleRouteController: RouteController<MainComposePath, MainCompose>()
