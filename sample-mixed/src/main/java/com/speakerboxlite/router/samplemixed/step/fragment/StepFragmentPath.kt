package com.speakerboxlite.router.samplemixed.step.fragment

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.samplemixed.base.RouteControllerFragmentApp
import com.speakerboxlite.router.samplemixed.step.StepViewModel

class StepFragmentPath(val step: Int): RoutePath

@Route
abstract class StepFragmentRouteController: RouteControllerFragmentApp<StepFragmentPath, StepViewModel, StepFragment>()
{
    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: StepFragmentPath): StepViewModel =
        modelProvider.getViewModel { StepViewModel(path.step, it) }
}