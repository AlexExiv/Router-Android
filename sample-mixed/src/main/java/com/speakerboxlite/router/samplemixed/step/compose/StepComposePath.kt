package com.speakerboxlite.router.samplemixed.step.compose

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.compose.AndroidComposeViewModelProvider
import com.speakerboxlite.router.samplemixed.base.RouteControllerComposeApp
import com.speakerboxlite.router.samplemixed.step.StepViewModel

class StepComposePath(val step: Int): RoutePath

@Route
abstract class StepComposeRouteController: RouteControllerComposeApp<StepComposePath, StepViewModel, StepView>()
{
    override fun onCreateViewModel(modelProvider: AndroidComposeViewModelProvider, path: StepComposePath): StepViewModel =
        modelProvider.getViewModel { StepViewModel(path.step, it) }
}
