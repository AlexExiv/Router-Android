package com.speakerboxlite.router.samplecompose.tabs

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.annotations.Tabs
import com.speakerboxlite.router.controllers.RouteController

class TabsPath: RoutePath

@Tabs
@Route
abstract class TabsRouteController: RouteController<TabsPath, TabsView>()
