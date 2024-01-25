package com.speakerboxlite.router.samplecompose.main

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.samplecompose.base.RouteControllerApp

class MainPath: RoutePath

@Route
abstract class MainRouteController: RouteControllerApp<MainPath, MainViewModel, MainView>()
