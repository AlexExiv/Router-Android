package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.RouteType
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterModelProvider
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.annotations.Presentation
import java.io.Serializable
import kotlin.reflect.KClass

interface RouteControllerInterface<Path: RoutePath, V: View>
{
    val singleTop: Boolean
    val creatingInjector: Boolean
    val preferredPresentation: Presentation
    val isChain: Boolean
    val isCompose: Boolean
    val routeType: RouteType
    val isTabs: Boolean

    val middlewares: List<MiddlewareController>

    val params: Serializable?

    fun check(url: String): Boolean
    fun check(path: RoutePath): Boolean
    fun convert(url: String): RoutePath

    fun isPartOfChain(clazz: KClass<*>): Boolean

    fun animationController(): AnimationController?
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
    fun onPrepareView(router: Router, view: V, path: Path)
}

interface Component

interface RouteControllerComponent<Path: RoutePath, V: View, C: Component>
{
    val componentClass: KClass<C>

    fun onInject(component: Any) {}

    fun onCreateInjector(path: Path, component: Any): Any
    fun onPrepareView(router: Router, view: V, path: Path, component: Any)
}

interface RouteControllerViewModelProvider<Path: RoutePath, VM: ViewModel>
{
    fun onProvideViewModel(modelProvider: RouterModelProvider, path: Path): VM
}

interface RouteControllerViewModelHolder<VM: ViewModel>
{
    fun onPrepareViewModel(router: Router, key: String, vm: VM)
}

interface RouteControllerViewModelHolderComponent<VM: ViewModel>
{
    fun onPrepareViewModel(router: Router, key: String, vm: VM, component: Any)
}
