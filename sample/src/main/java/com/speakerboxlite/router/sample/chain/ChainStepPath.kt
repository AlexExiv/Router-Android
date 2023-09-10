package com.speakerboxlite.router.sample.chain

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class ChainStepPath(val step: Int): RoutePath

@Route
abstract class ChainStepRouteController: RouteControllerApp<ChainStepPath, ChainViewModel, ChainFragment>()
{
    override fun onCreateViewModel(view: ChainFragment, path: ChainStepPath): ChainViewModel =
        view.getAndroidViewModel { ChainViewModel(path.step, it) }
}

