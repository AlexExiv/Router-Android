package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.ViewResultData
import com.speakerboxlite.router.annotations.Presentation

data class RouteParams<Path: RoutePath>(val execRouter: Router? = null,
                                        val path: Path,
                                        val presentation: Presentation? = null,
                                        val isReplace: Boolean = false,
                                        val tabIndex: Int? = null,
                                        val result: ViewResultData? = null)

typealias RouteParamsGen = RouteParams<RoutePath>

/**
 *
 */
interface MiddlewareController
{
    /**
     * Called when the router attempts to leave this route and navigate to another path (`next`).
     * This method is called after the `onBeforeRoute` method of the current route controller.
     *
     * @param router   The current active router.
     * @param current  The path of this route.
     * @param next     RouteParams that contains the path to which the router is attempting to navigate.
     * @return `true` to prevent navigation to the `next` path (use this value if you are routing to another path); `false` otherwise.
     */
    fun onBeforeRoute(router: Router, current: RoutePath, next: RouteParamsGen): Boolean = false

    /**
     * Called when the router attempts to navigate to this route (`current`).
     * This method is called before the `onRoute` method of the next route controller.
     *
     * @param router   The current active router. You can use it to navigate to another route, but don't forget to return `true`.
     * @param prev     The path of the previous route.
     * @param current  RouteParams that contains the path to which the router is attempting to navigate.
     * @return `true` to prevent navigation to the `current` path (use this value if you are routing to another path); `false` otherwise.
     */
    fun onRoute(router: Router, prev: RoutePath?, next: RouteParamsGen): Boolean = false

    /**
     * Called when the router is about to close this route.
     * This method is called before the `onClose` method of the closing route controller.
     *
     * @param router   The current active router. You can use it to navigate to another route, but don't forget to return `true`.
     * @param current  The path of the current route.
     * @param prev     The path of the previous route.
     * @return `true` to halt the dispatching of other middlewares (use this value if you are routing to another path); `false` otherwise.
     */
    fun onClose(router: Router, current: RoutePath, prev: RoutePath?): Boolean = false
}

/**
 * Implement this Middleware Controller if you need to inject dependencies into the controller
 */
interface MiddlewareControllerComponent: MiddlewareController
{
    /**
     * Override this method if you want to inject dependencies into the MiddlewareController.
     *
     * @param component Common AppComponent for this application.
     */
    fun onInject(component: Any)
}