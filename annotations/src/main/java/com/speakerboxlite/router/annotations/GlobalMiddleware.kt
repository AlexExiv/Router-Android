package com.speakerboxlite.router.annotations

/**
 * Annotates `MiddlewareController` as a global middleware. Global middlewares are dispatched after all other middlewares.
 *
 * The sequence of middleware execution:
 * 1. `RouteController`
 * 2. @Middleware controllers
 * 3. @GlobalMiddleware controllers
 *
 * @property order The order of calling for global middlewares.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GlobalMiddleware(val order: Int)
