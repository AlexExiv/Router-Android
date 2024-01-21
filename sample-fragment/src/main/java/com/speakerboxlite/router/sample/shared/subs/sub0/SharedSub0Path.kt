package com.speakerboxlite.router.sample.shared.subs.sub0

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.shared.RouteControllerShared

class SharedSub0Path: RoutePath

@Route
abstract class SharedSub0RouteController: RouteControllerShared<SharedSub0Path, SharedSub0ViewModel, SharedSub0Fragment>()
