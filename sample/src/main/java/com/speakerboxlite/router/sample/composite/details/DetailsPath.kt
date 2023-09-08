package com.speakerboxlite.router.sample.composite.details

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.base.RouteControllerApp

class DetailsPath: RoutePath

@Route
abstract class DetailsRouteController: RouteControllerApp<DetailsPath, DetailsViewModel, DetailsFragment>()
