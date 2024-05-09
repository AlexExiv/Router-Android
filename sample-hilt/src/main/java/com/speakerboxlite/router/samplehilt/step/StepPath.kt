package com.speakerboxlite.router.samplehilt.step

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.samplehilt.base.AndroidHiltViewModelProvider
import com.speakerboxlite.router.samplehilt.base.RouteControllerApp

class StepPath(val step: Int): RoutePath

@Route
abstract class StepRouteController: RouteControllerApp<StepPath, StepViewModel, StepView>()
{
    override fun onCreateViewModel(modelProvider: AndroidHiltViewModelProvider, path: StepPath): StepViewModel =
        modelProvider.getViewModel { f: StepViewModel.Factory -> f.create(path.step) }
}
