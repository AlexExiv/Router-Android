package com.speakerboxlite.router.sample.pro

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class ProPath(val refPath: RouteParamsGen): RoutePath

@Route(presentation = Presentation.Modal)
abstract class ProRouteController: RouteControllerApp<ProPath, ProViewModel, ProFragment>()
{
    override fun onCreateViewModel(view: ProFragment, path: ProPath): ProViewModel =
        view.getAndroidViewModel { ProViewModel(path.refPath, it) }
}
