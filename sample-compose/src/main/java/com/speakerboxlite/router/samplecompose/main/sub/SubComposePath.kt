package com.speakerboxlite.router.samplecompose.main.sub

import androidx.compose.runtime.Immutable
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

@Immutable
class SubComposePath: RoutePath

@Route
abstract class SubComposeRouteController: RouteController<SubComposePath, SubComposeView>()


