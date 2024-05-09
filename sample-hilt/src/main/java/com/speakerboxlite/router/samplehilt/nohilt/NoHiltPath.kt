package com.speakerboxlite.router.samplehilt.nohilt

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.samplehilt.base.AndroidHiltViewModelProvider
import com.speakerboxlite.router.samplehilt.base.RouteControllerApp

class NoHiltPath(val step: Int): RoutePath

@Route
abstract class NoHiltRouteController: RouteControllerApp<NoHiltPath, NoHiltViewModel, NoHiltView>()
{
    override fun onCreateViewModel(modelProvider: AndroidHiltViewModelProvider, path: NoHiltPath): NoHiltViewModel =
        modelProvider.getViewModelApp { NoHiltViewModel(path.step, it) }
}
