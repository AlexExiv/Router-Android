package com.speakerboxlite.router.sample.composite.details

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.composite.RouteControllerComposite

class DetailsPath: RoutePath

@Route
abstract class DetailsRouteController: RouteControllerComposite<DetailsPath, DetailsViewModel, DetailsFragment>()
