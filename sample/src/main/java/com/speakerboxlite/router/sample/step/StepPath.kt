package com.speakerboxlite.router.sample.step

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class StepPath(val step: Int): RoutePath

@Route
abstract class RouteControllerStep: RouteControllerApp<StepPath, StepViewModel, StepFragment>()
{
    override fun onCreateViewModel(view: StepFragment, path: StepPath): StepViewModel =
        view.getAndroidViewModel { StepViewModel(path.step, it) }
}