package com.speakerboxlite.router.compose.main

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

class MainPath: RoutePath

@Route
abstract class MainRouteController: RouteController<MainPath, MainView>()
