package com.speakerboxlite.router.sample.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class TabSingletonPath(val index: Int): RoutePath

@Route(singleton = true)
abstract class TabSingletonRouteController: RouteControllerApp<TabSingletonPath, TabViewModel, TabFragment>()
{
    override fun onCreateViewModel(view: TabFragment, path: TabSingletonPath): TabViewModel =
        view.getAndroidViewModel { TabViewModel(path.index, it) }
}
