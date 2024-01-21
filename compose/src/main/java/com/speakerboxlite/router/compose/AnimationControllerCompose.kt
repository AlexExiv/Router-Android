package com.speakerboxlite.router.compose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import com.speakerboxlite.router.controllers.AnimationController
import java.io.Serializable

interface AnimationControllerCompose: AnimationController, Serializable
{
    fun prepareAnimation(navigator: ComposeNavigator, scope: AnimatedContentTransitionScope<StackEntry>): ContentTransform
}