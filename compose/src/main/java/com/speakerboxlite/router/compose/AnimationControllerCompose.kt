package com.speakerboxlite.router.compose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import java.io.Serializable

interface AnimationControllerCompose: Serializable
{
    fun prepareAnimation(navigator: ComposeNavigator, scope: AnimatedContentTransitionScope<StackEntry>): ContentTransform
}