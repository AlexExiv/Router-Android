package com.speakerboxlite.router.annotations

/**
 * Marks the application class whose package should receive the generated RouterComponentImpl.
 *
 * KSP does not expose the same root-element heuristics as KAPT in a stable way, so the generated
 * component package is explicit. Use this on the application class that creates RouterComponentImpl.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class RouterApp
