package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.Result
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.annotations.Presentation

data class RouteParams<Path: RoutePath>(val execRouter: Router? = null,
                                        val path: Path,
                                        val presentation: Presentation? = null,
                                        val isReplace: Boolean = false,
                                        val tabIndex: Int? = null,
                                        val result: Result<Any>? = null)

typealias RouteParamsGen = RouteParams<RoutePath>

interface MiddlewareController
{
    fun onBeforeRoute(router: Router, current: RoutePath, next: RouteParamsGen): Boolean = false
    fun onRoute(router: Router, prev: RoutePath?, next: RouteParamsGen): Boolean = false
    fun onClose(router: Router, current: RoutePath, prev: RoutePath?): Boolean = false
}

interface MiddlewareControllerComponent: MiddlewareController
{
    fun onInject(component: Any)
}