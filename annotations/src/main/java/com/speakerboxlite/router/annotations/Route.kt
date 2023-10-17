package com.speakerboxlite.router.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Route(
    /**
     * Relative `URL` path to the controller.
     */
    val uri: String = "",

    /**
     * The default presentation style used in the absence of a specified style in the `route` method of the `Router`.
     */
    val presentation: Presentation = Presentation.Push,

    /**
     * Indicates that only one view with this path can exist in the views' hierarchy.
     * If a view with this path is already in the hierarchy, the `Router` will close all views until it reaches this view.
     */
    val singleTop: Boolean = false,

    /**
     * The class file of the animation inherited from `AnimationController`
     */
    val animation: KClass<*> = Nothing::class)
