package com.speakerboxlite.router.sample.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp

class TabPath0: RoutePath

@Route
abstract class TabRouteController: RouteControllerApp<TabPath0, TabViewModel, TabFragment>()
{
    override fun onCreateViewModel(view: TabFragment, path: TabPath0): TabViewModel =
        view.getAndroidViewModel { TabViewModel(0, it) }
}
