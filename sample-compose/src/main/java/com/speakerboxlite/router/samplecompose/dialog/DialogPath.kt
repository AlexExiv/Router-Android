package com.speakerboxlite.router.samplecompose.dialog

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

class DialogPath: RoutePath

@Route
abstract class DialogRouteController: RouteController<DialogPath, DialogView>()
