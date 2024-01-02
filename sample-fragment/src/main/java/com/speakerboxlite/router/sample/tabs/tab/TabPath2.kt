package com.speakerboxlite.router.sample.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp
import com.speakerboxlite.router.sample.base.middlewares.MiddlewareAuth

class TabPath2: RoutePath

@Route
@MiddlewareAuth
abstract class TabAuthRouteController: RouteControllerApp<TabPath2, TabViewModel, TabFragment>()
{
    override fun onCreateViewModel(view: TabFragment, path: TabPath2): TabViewModel =
        view.getAndroidViewModel { TabViewModel(2, it) }
}
