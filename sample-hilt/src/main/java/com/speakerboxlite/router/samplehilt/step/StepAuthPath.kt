package com.speakerboxlite.router.samplehilt.step

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.androidhilt.AndroidHiltViewModelProvider
import com.speakerboxlite.router.samplehilt.base.RouteControllerApp
import com.speakerboxlite.router.samplehilt.base.middlewares.MiddlewareAuth

class StepAuthPath(val step: Int): RoutePath

@MiddlewareAuth
@Route
abstract class StepAuthRouteController: RouteControllerApp<StepAuthPath, StepViewModel, StepView>()
{
    override fun onCreateViewModel(modelProvider: AndroidHiltViewModelProvider, path: StepAuthPath): StepViewModel =
        modelProvider.getViewModel { f: StepViewModel.Factory -> f.create(path.step) }
}