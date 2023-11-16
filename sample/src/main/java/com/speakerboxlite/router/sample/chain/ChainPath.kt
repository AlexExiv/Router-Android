package com.speakerboxlite.router.sample.chain

import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.annotations.Chain
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp
import com.speakerboxlite.router.sample.base.animations.AnimationControllerBottomToTop

data class ChainPath(val step: Int): RoutePathResult<Int>

@Chain([ChainStepPath::class])
@Route(animation = AnimationControllerBottomToTop::class)
abstract class ChainRouteController: RouteControllerApp<ChainPath, ChainViewModel, ChainFragment>()
{
    override fun onCreateViewModel(view: ChainFragment, path: ChainPath): ChainViewModel =
        view.getAndroidViewModel { ChainViewModel(path.step, it) }
}
