package com.speakerboxlite.router.sample.compose.step

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

data class StepComposePath(val step: Int): RoutePath

@Route
class StepComposeRouteController: RouteController<StepComposePath, StepCompose>()
{
    override fun onCreateView(path: StepComposePath): StepCompose = StepCompose(path.step)
}
