package com.speakerboxlite.router.sample.step.replace

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class ReplacePath(val step: Int): RoutePath

@Route
abstract class ReplaceRouteController: RouteControllerApp<ReplacePath, ReplaceViewModel, ReplaceFragment>()
{
    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: ReplacePath): ReplaceViewModel =
        modelProvider.getViewModel { ReplaceViewModel(path.step, it) }
}