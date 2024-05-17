package com.speakerboxlite.router.sample.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.fragment.FragmentViewModelProvider
import com.speakerboxlite.router.sample.base.RouteControllerApp

class TabPath1: RoutePath

@Route
abstract class TabSingletonRouteController: RouteControllerApp<TabPath1, TabViewModel, TabFragment>()
{
    override fun onCreateViewModel(modelProvider: FragmentViewModelProvider, path: TabPath1): TabViewModel =
        modelProvider.getViewModel { TabViewModel(1, it) }
}
