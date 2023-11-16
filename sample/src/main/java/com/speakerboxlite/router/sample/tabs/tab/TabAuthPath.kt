package com.speakerboxlite.router.sample.tabs.tab

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp
import com.speakerboxlite.router.sample.base.middlewares.MiddlewareAuth

data class TabAuthPath(val index: Int): RoutePath

@Route(singleTop = true)
@MiddlewareAuth
abstract class TabAuthRouteController: RouteControllerApp<TabAuthPath, TabViewModel, TabFragment>()
{
    override fun onCreateViewModel(view: TabFragment, path: TabAuthPath): TabViewModel =
        view.getAndroidViewModel { TabViewModel(path.index, it) }
}
