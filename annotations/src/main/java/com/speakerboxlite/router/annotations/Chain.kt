package com.speakerboxlite.router.annotations

import kotlin.reflect.KClass

/**
 * Defines a sequence of screens united for a common purpose. When the user achieves this goal and taps on a finishing action
 * (calling the 'close' method), the entire sequence closes back to the screen that initiated this sequence.
 * The resulting data of the sequence (if it exists) is delivered to that screen, regardless of the screen from which the send event was called.
 *
 * @param closeItems An array of `RoutePath` classes on which a close event leads to the closing of the sequence.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Chain(val closeItems: Array<KClass<*>>)
