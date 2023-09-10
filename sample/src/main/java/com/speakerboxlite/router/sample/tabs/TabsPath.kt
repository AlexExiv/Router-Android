package com.speakerboxlite.router.sample.tabs

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.base.RouteControllerApp

class TabsPath: RoutePath

@Route
abstract class TabsRouteController: RouteControllerApp<TabsPath, TabsViewModel, TabsFragment>()