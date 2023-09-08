package com.speakerboxlite.router.sample.composite

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.base.RouteControllerApp

class CompositePath: RoutePath

@Route
abstract class CompositeRouteController: RouteControllerApp<CompositePath, CompositeViewModel, CompositeFragment>()
