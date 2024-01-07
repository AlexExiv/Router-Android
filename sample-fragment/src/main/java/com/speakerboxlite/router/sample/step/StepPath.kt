package com.speakerboxlite.router.sample.step

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.sample.base.RouteControllerApp
import com.speakerboxlite.router.sample.base.middlewares.MiddlewarePro

data class StepPath(val step: Int): RoutePath

@Route("/steps/{id}")
@MiddlewarePro
abstract class RouteControllerStep: RouteControllerApp<StepPath, StepViewModel, StepFragment>()
{
    override fun convert(path: Map<String, String>, query: Map<String, String>): StepPath =
        StepPath(path["id"]!!.toInt())

    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: StepPath): StepViewModel =
        modelProvider.getViewModel { StepViewModel(path.step, it) }
}