package com.speakerboxlite.router.compose.bts

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

class BottomSheetPath: RoutePath

@Route
abstract class BottomRouteController: RouteController<BottomSheetPath, BottomSheetView>()
