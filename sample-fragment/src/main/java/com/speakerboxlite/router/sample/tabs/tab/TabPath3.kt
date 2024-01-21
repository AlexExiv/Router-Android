package com.speakerboxlite.router.sample.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.sample.base.RouteControllerApp

class TabPath3: RoutePath

@Route
abstract class TabRoute3Controller: RouteControllerApp<TabPath3, TabViewModel, TabFragment>()
{
    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: TabPath3): TabViewModel =
        modelProvider.getViewModel { TabViewModel(3, it) }
}
