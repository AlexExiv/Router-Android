package com.speakerboxlite.router.compose

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset

val ContentTransformNone = ContentTransform(EnterTransition.None, ExitTransition.None)

private val AnimationSpecChangeTab = spring(
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = IntOffset.VisibilityThreshold
)

fun contentTransformChangeTab(prevTab: Int, nextTab: Int) =
    slideInHorizontally(tween(300), { if (prevTab < nextTab) it else -it }) togetherWith
        slideOutHorizontally(tween(300), { if (prevTab < nextTab) -it else it })