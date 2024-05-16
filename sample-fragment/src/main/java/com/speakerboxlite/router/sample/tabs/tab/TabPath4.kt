package com.speakerboxlite.router.sample.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.fragment.FragmentViewModelProvider
import com.speakerboxlite.router.sample.base.RouteControllerApp

class TabPath4: RoutePath

@Route
abstract class TabRoute4Controller: RouteControllerApp<TabPath4, TabViewModel, TabFragment>()
{
    override fun onCreateViewModel(modelProvider: FragmentViewModelProvider, path: TabPath4): TabViewModel =
        modelProvider.getViewModel { TabViewModel(4, it) }
}
