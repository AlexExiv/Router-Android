package com.speakerboxlite.router.compose.step

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

class StepPath(val step: Int): RoutePath

@Route
class StepRouteController: RouteController<StepPath, StepView>()
{
    override fun onCreateView(path: StepPath): StepView = StepView()
        .also {
            it.step.intValue = path.step
        }
}
