package com.speakerboxlite.router.sample.tabs

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.annotations.Tabs
import com.speakerboxlite.router.sample.base.RouteControllerApp
import com.speakerboxlite.router.sample.base.middlewares.MiddlewareAuth
import com.speakerboxlite.router.sample.base.middlewares.MiddlewarePro

class TabsPath: RoutePath

@Tabs
@Route
//@MiddlewareAuth
//@MiddlewarePro
abstract class TabsRouteController: RouteControllerApp<TabsPath, TabsViewModel, TabsFragment>()