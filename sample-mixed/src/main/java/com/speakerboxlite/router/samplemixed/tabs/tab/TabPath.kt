package com.speakerboxlite.router.samplemixed.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController
import com.speakerboxlite.router.fragment.FragmentViewModelProvider
import com.speakerboxlite.router.samplemixed.base.RouteControllerFragmentApp
import com.speakerboxlite.router.samplemixed.tabs.tab.compose.TabView
import com.speakerboxlite.router.samplemixed.tabs.tab.fragment.TabFragment

class TabPath0: RoutePath

@Route
abstract class Tab0RouteController: RouteControllerFragmentApp<TabPath0, TabViewModel, TabFragment>()
{
    override fun onCreateViewModel(modelProvider: FragmentViewModelProvider, path: TabPath0): TabViewModel =
        modelProvider.getViewModel { TabViewModel(0, it) }
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
/*
@Route
abstract class Tab2RouteController: RouteControllerFragmentApp<TabPath2, StepViewModel, StepFragment>()
{
    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: TabPath2): StepViewModel =
        modelProvider.getViewModel { StepViewModel(11, it) }
}*/
