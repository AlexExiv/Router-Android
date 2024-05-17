package com.speakerboxlite.router.sample.chain.sub

import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.fragment.FragmentViewModelProvider
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class SubChainPath(val step: Int): RoutePathResult<Int>

@Route
abstract class SubChainRouteController: RouteControllerApp<SubChainPath, SubChainViewModel, SubChainFragment>()
{
    override fun onCreateViewModel(modelProvider: FragmentViewModelProvider, path: SubChainPath): SubChainViewModel =
        modelProvider.getViewModel { SubChainViewModel(path.step, it) }
}
