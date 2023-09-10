package com.speakerboxlite.router.sample.chain.sub

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class SubChainPath(val step: Int): RoutePath

@Route
abstract class SubChainRouteController: RouteControllerApp<SubChainPath, SubChainViewModel, SubChainFragment>()
{
    override fun onCreateViewModel(view: SubChainFragment, path: SubChainPath): SubChainViewModel =
        view.getAndroidViewModel { SubChainViewModel(path.step, it) }
}
