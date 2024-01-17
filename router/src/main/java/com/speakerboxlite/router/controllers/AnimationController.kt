package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.View
import com.speakerboxlite.router.annotations.Presentation

interface AnimationController

enum class AnimationHostChanged
{
    FromFragment, FromCompose
}

fun interface AnimationControllerFactory
{
    fun onCreate(presentation: Presentation?, view: View?, hostChanged: AnimationHostChanged?): AnimationController?
}
