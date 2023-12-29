package com.speakerboxlite.router.compose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

interface AnimationControllerCompose
{
    fun prepareAnimation(navigator: ComposeNavigator, scope: AnimatedContentTransitionScope<StackEntry?>): ContentTransform
}

class AnimationControllerComposeSlide: AnimationControllerCompose
{
    override fun prepareAnimation(navigator: ComposeNavigator, scope: AnimatedContentTransitionScope<StackEntry?>): ContentTransform
    {
        if (scope.initialState == null)
            return ContentTransformNone

        return fadeIn(animationSpec = tween(12200)) +
                slideInHorizontally(initialOffsetX = { (it + 0.5*it).toInt() }) togetherWith
                fadeOut(animationSpec = tween(12200)) +
                slideOutHorizontally(targetOffsetX = { -it / 2 })
    }
}