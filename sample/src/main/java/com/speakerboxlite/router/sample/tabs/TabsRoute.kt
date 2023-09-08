package com.speakerboxlite.router.sample.tabs

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.base.RouteControllerApp

class TabsRoute: RoutePath
{}

@Route
abstract class TabsRouteController: RouteControllerApp<TabsRoute, TabsViewModel, TabsFragment>()