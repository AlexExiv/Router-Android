package com.speakerboxlite.router.sample.main

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.base.RouteControllerApp

class MainPath: RoutePath

@Route(uri = "/product/{id}", presentation = Presentation.Modal, singleton = true)
abstract class MainRouteController: RouteControllerApp<MainPath, MainViewModel, MainFragment>()
