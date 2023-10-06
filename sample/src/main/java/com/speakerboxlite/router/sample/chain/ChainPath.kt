package com.speakerboxlite.router.sample.chain

import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp
import com.speakerboxlite.router.sample.base.animations.AnimationControllerBottomToTop
import kotlin.reflect.KClass

data class ChainPath(val step: Int): RoutePathResult<Int>

@Route(animation = AnimationControllerBottomToTop::class)
abstract class ChainRouteController: RouteControllerApp<ChainPath, ChainViewModel, ChainFragment>()
{
    override val chainPaths: List<KClass<*>> get() = listOf(ChainStepPath::class)

    override fun onCreateViewModel(view: ChainFragment, path: ChainPath): ChainViewModel =
        view.getAndroidViewModel { ChainViewModel(path.step, it) }
}
