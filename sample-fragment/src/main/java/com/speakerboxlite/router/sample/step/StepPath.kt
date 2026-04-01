package com.speakerboxlite.router.sample.step

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.annotations.SingleTop
import com.speakerboxlite.router.fragment.FragmentViewModelProvider
import com.speakerboxlite.router.sample.base.RouteControllerApp
import com.speakerboxlite.router.sample.base.middlewares.MiddlewarePro

open class StepPath(val step: Int): RoutePath

class ConstStepPath: StepPath(100)

@Route("/steps/{id}")
@MiddlewarePro
abstract class RouteControllerStep: RouteControllerApp<StepPath, StepViewModel, StepFragment>()
{
    override fun convert(path: Map<String, String>, query: Map<String, String>): StepPath =
        StepPath(path["id"]!!.toInt())

    override fun onCreateViewModel(modelProvider: FragmentViewModelProvider, path: StepPath): StepViewModel =
        modelProvider.getViewModel { StepViewModel(path.step, it) }

    override fun onUpdateData(path: StepPath, vm: StepViewModel)
    {
        vm.updateStep(path.step)
    }
}

data class StepSinglePath(val step: Int): RoutePath

@Route(singleTop = SingleTop.Equal)
@MiddlewarePro
abstract class RouteControllerStepSingle: RouteControllerApp<StepSinglePath, StepViewModel, StepFragment>()
{
    override fun onCreateViewModel(modelProvider: FragmentViewModelProvider, path: StepSinglePath): StepViewModel =
        modelProvider.getViewModel { StepViewModel(path.step, it) }
}