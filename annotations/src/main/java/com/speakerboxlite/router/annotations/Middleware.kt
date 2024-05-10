package com.speakerboxlite.router.annotations

/**
 * Use this annotation to create your own Middleware annotations
 *
 * @sample
 * `@Middleware`
 * `annotation class MiddlewareAuth`
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Middleware()
