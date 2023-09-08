package com.speakerboxlite.router.sample.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class TabPath(val index: Int): RoutePath

@Route
abstract class TabRouteController: RouteControllerApp<TabPath, TabViewModel, TabFragment>()
{
    override fun onCreateViewModel(view: TabFragment, path: TabPath): TabViewModel =
        view.getAndroidViewModel { TabViewModel(path.index, it) }
}
