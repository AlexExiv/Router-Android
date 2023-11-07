package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.View
import com.speakerboxlite.router.annotations.Presentation
import java.io.Serializable
import kotlin.reflect.KClass

interface RouteControllerInterface<Path: RoutePath, V: View>
{
    val singleTop: Boolean
    val creatingInjector: Boolean
    val preferredPresentation: Presentation
    val isChain: Boolean

    val middlewares: List<MiddlewareController>

    val params: Serializable?

    fun check(url: String): Boolean
    fun check(path: RoutePath): Boolean
    fun convert(url: String): RoutePath

    fun isPartOfChain(clazz: KClass<*>): Boolean

    fun animationController(): AnimationController<RoutePath, View>?
    fun onCreateView(path: Path): V

    /**
     * Called when the router attempts to leave this route and navigate to another path (`next`).
     *
     * @param router   The current active router.
     * @param current  The path of this route.
     * @param next     The path to which the router is attempting to navigate.
     * @param result   The callback through which this route expects to receive a result.
     * @return `true` to prevent navigation to the `next` path (use this value if you are routing to another path); `false` otherwise.
     */
    fun onBeforeRoute(router: Router, current: Path, next: RouteParamsGen): Boolean

    /**
     * Called when the router attempts to navigate to this route (`current`).
     *
     * @param router   The current active router. You can use it to navigate to another route, but don't forget to return `true`.
     * @param prev     The path of the previous route.
     * @param current  The path to which the router is attempting to navigate.
     * @param result   The callback through which the previous route expects to receive a result.
     * @return `true` to prevent navigation to the `current` path (use this value if you are routing to another path); `false` otherwise.
     */
    fun onRoute(router: Router, prev: RoutePath?, current: RouteParams<Path>): Boolean

    /**
     * Called when the router is about to close this route.
     *
     * @param router   The current active router. You can use it to navigate to another route, but don't forget to return `true`.
     * @param current  The path of the current route.
     * @param prev     The path of the previous route.
     * @return `true` to halt the dispatching of other middlewares (use this value if you are routing to another path); `false` otherwise.
     */
    fun onClose(router: Router, current: Path, prev: RoutePath?): Boolean
}

interface RouteControllerComposable<Path: RoutePath, V: View>
{
    fun onComposeView(router: Router, view: V, path: Path)
}

interface Component

interface RouteControllerComponent<Path: RoutePath, V: View, C: Component>
{
    val componentClass: KClass<C>

    fun onCreateInjector(path: Path, component: Any): Any
    fun onComposeView(router: Router, view: V, path: Path, component: Any)
}