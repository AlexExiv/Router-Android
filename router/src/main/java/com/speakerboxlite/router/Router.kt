package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.CommandExecutor

typealias Result<R> = (R) -> Unit

interface Router
{
    /** Current top router **/
    var topRouter: Router?

    /**
     * Returns information about whether the stack has a previous screen.
     */
    val hasPreviousScreen: Boolean

    /**
     * Navigate to a specific URL path.
     *
     * @param url The relative URL path. Avoid using absolute URLs. This URL should correspond to a path defined by the @Route annotation.
     * @return A unique view key associated with the displayed screen, or null if the URL path was not found.
     */
    fun route(url: String): String?

    /**
     * Navigate to a screen using the specified path and presentation type.
     *
     * @param path          The path to the screen connected by the RouteController.
     * @param presentation  The type of presentation (e.g., modal, full-screen).
     * @return              A unique identifier (view key) associated with the displayed screen.
     * @throws RouteNotFoundException If the provided path is not found in the routes manager.
     */
    fun route(path: RoutePath, presentation: Presentation = Presentation.Push): String

    /**
     * Navigate to a screen with an expected result.
     *
     * @param path         The path to the screen connected by the `RouteController`.
     * @param presentation The type of presentation (e.g., modal, full-screen).
     * @param result       The callback for handling the screen result. To send a result, use `ResultProvider::send`.
     * @return             A unique view key associated with the displayed screen.
     * @throws RouteNotFoundException If the provided path is not found in the routes manager.
     */
    fun <R: Any> routeWithResult(path: RoutePath, presentation: Presentation = Presentation.Push, result: Result<R>): String

    /**
     * Show a dialog screen
     *
     * @param path The path to the dialog connected by the `RouteController`
     */
    fun routeDialog(path: RoutePath)

    /**
     * Show a dialog screen with an expected result.
     *
     * @param path The path to the dialog connected by the `RouteController`
     * @param result The callback for handling the screen result. To send a result, use `ResultProvider::send`.
     */
    fun <R: Any> routeDialogWithResult(path: RoutePath, result: Result<R>)

    /**
     * Closes the top view in the router. This method should be called when handling a physical Back button click
     * or a toolbar back button click. Unlike the `close()` method, this method does not close a chain, even if
     * the view's path is part of that chain.
     */
    fun back()

    /**
     * Closes the top view in the router. This method should be called when user click `apply` button or something like that.
     * Unlike the `back()` method, this method close a chain if the view's path is a part of that chain
     */
    fun close()

    /**
     * Closes all views in the router up to a specific view's key.
     *
     * @param key The view's key to which all views will be closed.
     */
    fun closeTo(key: String)

    /**
     * Closes all views
     */
    fun closeToTop()

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
     * This method compose view injects ViewModel, Router, ResultProvider. This method should be called in the fragment's `onCreate` method
     */
    fun onComposeView(view: View<*>)

    /**
     * Creates a local router for the top view in the stack associated with the specified view key.
     *
     * @param key The view's key (viewKey) to which the new router belongs.
     * @return A new local router instance.
     */
    fun createRouterLocal(key: String): RouterLocal

    /**
     * Creates tabs router for the top view in the stack
     */
    fun createRouterTabs(factory: HostViewFactory): RouterTabs

    /**
     * Removes a view from the views stack and resets all connections.
     * It's important to call this method in the `Fragment's` lifecycle method `onDestroy` to ensure that the view
     * has been removed from the views stack.
     * This is particularly important for dialog fragments that can be closed silently.
     *
     * @param key The view's key (viewKey) to be removed.
     */
    fun removeView(key: String)
}
