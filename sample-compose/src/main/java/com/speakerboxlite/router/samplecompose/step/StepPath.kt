package com.speakerboxlite.router.samplecompose.step

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.compose.AndroidComposeViewModelProvider
import com.speakerboxlite.router.samplecompose.base.RouteControllerApp

class StepPath(val step: Int): RoutePath

@Route
abstract class StepRouteController: RouteControllerApp<StepPath, StepViewModel, StepView>()
{
    override fun onCreateViewModel(modelProvider: AndroidComposeViewModelProvider, path: StepPath): StepViewModel =
        modelProvider.getViewModel { StepViewModel(path.step, it) }
}
