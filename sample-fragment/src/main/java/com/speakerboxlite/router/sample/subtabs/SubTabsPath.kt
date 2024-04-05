package com.speakerboxlite.router.sample.subtabs

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.base.RouteControllerApp

class SubTabsPath: RoutePath

@Route
abstract class SubTabsRouteController: RouteControllerApp<SubTabsPath, SubTabsViewModel, SubTabsFragment>()
