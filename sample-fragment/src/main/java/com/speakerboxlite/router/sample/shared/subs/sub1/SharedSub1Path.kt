package com.speakerboxlite.router.sample.shared.subs.sub1

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.shared.RouteControllerShared

class SharedSub1Path: RoutePath

@Route
abstract class SharedSub1RouteController: RouteControllerShared<SharedSub1Path, SharedSub1ViewModel, SharedSub1Fragment>()
