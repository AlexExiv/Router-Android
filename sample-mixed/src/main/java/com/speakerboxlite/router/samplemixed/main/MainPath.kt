package com.speakerboxlite.router.samplemixed.main

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.annotations.SingleTop
import com.speakerboxlite.router.samplemixed.base.RouteControllerFragmentApp

class MainPath: RoutePath

@Route(presentation = Presentation.Modal, singleTop = SingleTop.Class)
abstract class MainRouteController: RouteControllerFragmentApp<MainPath, MainViewModel, MainFragment>()
