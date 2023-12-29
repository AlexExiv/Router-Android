package com.speakerboxlite.router.compose.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

class TabPath0: RoutePath

@Route
class Tab0RouteController: RouteController<TabPath0, TabView>()
{
    override fun onCreateView(path: TabPath0): TabView = TabView(0)
}

class TabPath1: RoutePath

@Route
class Tab1RouteController: RouteController<TabPath1, TabView>()
{
    override fun onCreateView(path: TabPath1): TabView = TabView(1)
}

class TabPath2: RoutePath

@Route
class Tab2RouteController: RouteController<TabPath2, TabView>()
{
    override fun onCreateView(path: TabPath2): TabView = TabView(2)
}
