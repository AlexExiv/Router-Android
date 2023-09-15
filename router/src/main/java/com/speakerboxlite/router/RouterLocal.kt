package com.speakerboxlite.router

/**
 * Local router for a screen. Use it to manage and display sub-fragments within this screen.
 */
interface RouterLocal: Router
{
    /**
     * Displays a specific view, identified by its `path`, within the specified container.
     *
     * @param containerId The ID of the container within the screen where the view will be displayed.
     * @param path        The path to the sub view that should be shown.
     */
    fun routeInContainer(containerId: Int, path: RoutePath): String

    /**
     * Displays a specific view, identified by its `path`, within the specified container.
     *
     * @param containerId The ID of the container within the screen where the view will be displayed.
     * @param path        The path to the sub view that should be shown.
     * @param result      The callback for handling the screen result. To send a result, use `ResultProvider::send`.
     */
    fun <R: Any> routeInContainerWithResult(containerId: Int, path: RoutePath, result: Result<R>): String
}