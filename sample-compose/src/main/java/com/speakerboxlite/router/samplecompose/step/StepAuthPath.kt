package com.speakerboxlite.router.samplecompose.step

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.compose.AndroidComposeViewModelProvider
import com.speakerboxlite.router.samplecompose.base.RouteControllerApp
import com.speakerboxlite.router.samplecompose.base.middleware.MiddlewareAuth

class StepAuthPath(val step: Int): RoutePath

@MiddlewareAuth
@Route
abstract class StepAuthRouteController: RouteControllerApp<StepAuthPath, StepViewModel, StepView>()
{
    override fun onCreateViewModel(modelProvider: AndroidComposeViewModelProvider, path: StepAuthPath): StepViewModel =
        modelProvider.getViewModel { StepViewModel(path.step, it) }
}