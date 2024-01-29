package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.result.RouterResultProvider
import java.io.Serializable

data class Result<VR: ViewResult, R>(val vr: VR, val result: R)

fun interface RouterResultDispatcher<VR: ViewResult, R>: Serializable
{
    /**
     * This handler will be called to dispatch the result. It's important not to use `this` context of the caller in the dispatcher
     * because the caller could be recreated by this time. In the `vr` parameter, you will get the current instance of the caller.
     */
    fun onDispatch(result: Result<VR, R>)
}

interface Router
{
    /** Current top router **/
    val topRouter: Router?

    /**
     * Returns information about whether the stack has a previous screen.
     */
    val hasPreviousScreen: Boolean

    /**
     * Prevents the current screen from being closed when the back button is clicked. This action applies only to the current screen.
     * If you set this property to `true` and push another screen onto the stack, the new screen will have this property automatically set to `false`.
     */
    var lockBack: Boolean

    /**
     * Navigate to a specific URL path.
     *
     * @param url The relative URL path. Avoid using absolute URLs. This URL should correspond to a path defined by the @Route annotation.
     * @return A router that will execute the route; may be null (usually when a middleware interrupts the route).
     */
    fun route(url: String): Router?

    /**
     * Navigate to a screen using the specified path and presentation type.
     *
     * @param path          The path to the screen connected by the RouteController.
     * @param presentation  The type of presentation (e.g., modal, full-screen).
     * @return              A router that will execute the route; may be null (usually when a middleware interrupts the route).
     * @throws RouteNotFoundException If the provided path is not found in the routes manager.
     */
    fun route(path: RoutePath, presentation: Presentation? = null): Router?

    /**
     * Navigate to a screen with an expected result.
     *
     * @param viewResult   ViewResult's subclass it will be passed to the result dispatcher.
     * @param path         The path to the screen connected by the `RouteController`.
     * @param presentation The type of presentation (e.g., modal, full-screen).
     * @param result       The callback for handling the screen result. To send a result, use `ResultProvider::send`.
     * @return             A router that will execute the route; may be null (usually when a middleware interrupts the route).
     * @throws RouteNotFoundException If the provided path is not found in the routes manager.
     */
    fun <VR: ViewResult, R: Any> routeWithResult(viewResult: VR, path: RoutePathResult<R>, presentation: Presentation? = null, result: RouterResultDispatcher<VR, R>): Router?

    /**
     * Replaces the top screen on the stack with a new one specified by the given `path`.
     *
     * @param path The path to the screen connected by the `RouteController`.
     * @return A router that will execute the route; may be null (usually when a middleware interrupts the route).
     * @throws RouteNotFoundException If the provided path is not found in the routes manager.
     */
    fun replace(path: RoutePath): Router?

    /**
     * Navigate to a screen using the specified path and presentation type.
     *
     * @param path          The path to the screen connected by the RouteController.
     * @param presentation  The type of presentation (e.g., modal, full-screen).
     * @return A router that will execute the route; may be null (usually when a middleware interrupts the route).
     * @throws RouteNotFoundException If the provided path is not found in the routes manager.
     */
    fun route(path: RouteParamsGen): Router?

    /**
     * Closes the top view in the router. This method should be called when handling a physical Back button click
     * or a toolbar back button click. Unlike the `close()` method, this method does not close a chain, even if
     * the view's path is part of that chain.
     *
     * @return If the view stack is empty, returns the parent router; otherwise, returns this router. Null if the event hasn't occurred.
     */
    fun back(): Router?

    /**
     * Closes the top view in the router. This method should be called when user click `apply` button or something like that.
     * Unlike the `back()` method, this method close a chain if the view's path is a part of that chain
     *
     * @return If the view stack is empty, returns the parent router; otherwise, returns this router. Null if the event hasn't occurred.
     */
    fun close(): Router?

    /**
     * Closes all views in the router up to a specific view's key.
     *
     * @param key The view's key to which all views will be closed.
     *
     * @return If the view stack is empty, returns the parent router; otherwise, returns this router. Null if the event hasn't occurred.
     */
    fun closeTo(key: String): Router?

    /**
     * Closes all views
     *
     * @return If the view stack is empty, returns the parent router; otherwise, returns this router. Null if the event hasn't occurred.
     */
    fun closeToTop(): Router?

    /**
     * Binds a command executor. This method should be called in the `onResume()` methods of the activity and fragment.
     * Any saved operations will be immediately dispatched.
     *
     * @param executor The binding executor.
     */
    fun bindExecutor(executor: CommandExecutor)

    /**
     * Resets a command executor. This method should be called in the `onPause()` methods of the activity and fragment.
     * Any pending operations will be pushed onto the stack before calling `bindExecutor`.
     */
    fun unbindExecutor()

    /**
     * Prepares the view by injecting the ViewModel, Router, and ResultProvider and so on. This method should be called in the `onCreate` method of the fragment.
     */
    fun onPrepareView(view: View, viewModel: ViewModel?)

    fun <VM: ViewModel> provideViewModel(view: View, modelProvider: RouterModelProvider): VM

    /**
     * Composes the view's animation. This method should be called in the `onViewCreated` method of the fragment.
     */
    fun onComposeAnimation(view: View)

    /**
     * Creates a local router for the top view in the stack associated with the specified view key.
     *
     * @param key The view's key (viewKey) to which the new router belongs.
     * @return A new local router instance.
     */
    fun createRouterLocal(key: String): RouterLocal

    /**
     * Creates tabs router for the top view in the stack
     *
     * @param key The view's key (viewKey) to which the new router belongs.
     * @param presentInTab
     * @return A new tabs router instance.
     */
    fun createRouterTabs(key: String): RouterTabs

    /**
     * Removes a view from the views stack and resets all connections.
     * It's important to call this method in the `Fragment's` lifecycle method `onDestroy` to ensure that the view
     * has been removed from the views stack.
     * This is particularly important for dialog fragments that can be closed silently.
     *
     * @param key The view's key (viewKey) to be removed.
     */
    fun removeView(key: String)

    fun createResultProvider(key: String): RouterResultProvider
}
