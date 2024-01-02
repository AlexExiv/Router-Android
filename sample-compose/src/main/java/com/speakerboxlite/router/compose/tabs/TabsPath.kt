package com.speakerboxlite.router.compose.tabs

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

class TabsPath: RoutePath

@Route
abstract class TabsRouteController: RouteController<TabsPath, TabsView>()
