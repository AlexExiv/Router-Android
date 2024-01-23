package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.View
import com.speakerboxlite.router.annotations.Presentation

interface AnimationController

fun interface AnimationControllerFactory
{
    fun onCreate(presentation: Presentation?, view: View): AnimationController?
}
