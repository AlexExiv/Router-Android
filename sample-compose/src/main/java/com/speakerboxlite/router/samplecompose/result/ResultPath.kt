package com.speakerboxlite.router.samplecompose.result

import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.samplecompose.base.RouteControllerApp

class ResultPath: RoutePathResult<String>

@Route
abstract class ResultRouteController: RouteControllerApp<ResultPath, ResultViewModel, ResultView>()
