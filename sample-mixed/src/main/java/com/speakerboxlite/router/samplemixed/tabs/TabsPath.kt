package com.speakerboxlite.router.samplemixed.tabs

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.annotations.Tabs
import com.speakerboxlite.router.samplemixed.base.RouteControllerFragmentApp

class TabsPath: RoutePath

@Tabs
@Route
abstract class TabsRouteController: RouteControllerFragmentApp<TabsPath, TabsViewModel, TabsFragment>()
