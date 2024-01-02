package com.speakerboxlite.router.compose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import com.speakerboxlite.router.ViewBTS
import com.speakerboxlite.router.ViewDialog

interface AnimationControllerCompose
{
    fun prepareAnimation(navigator: ComposeNavigator, scope: AnimatedContentTransitionScope<StackEntry>): ContentTransform
}

class AnimationControllerComposeSlide: AnimationControllerCompose
{
    override fun prepareAnimation(navigator: ComposeNavigator, scope: AnimatedContentTransitionScope<StackEntry>): ContentTransform
    {
        if (navigator.lastFullItem == null)
            return ContentTransformNone

        val lastEntry = navigator.poppingEntries?.lastOrNull()
        val isPopping = if (lastEntry?.view is ViewDialog || lastEntry?.view is ViewBTS)
            false
        else
            lastEntry?.isRemoving == true

        val animTime = 300
        val fadeIn = fadeIn(animationSpec = tween(animTime)) +
                slideInHorizontally(animationSpec = tween(animTime), initialOffsetX = { if (isPopping) -it/2 else it })
        val fadeOut = fadeOut(animationSpec = tween(animTime)) +
                slideOutHorizontally(animationSpec = tween(animTime), targetOffsetX = { if (isPopping) it else -it/2 })

        return ContentTransform(fadeIn, fadeOut, navigator.size.toFloat())
    }
}