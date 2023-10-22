package com.speakerboxlite.router

import com.speakerboxlite.router.command.CommandExecutor

typealias OnTabChangeCallback = (Int) -> Unit

/**
 * Use this router in the tab's Adapter class
 */
interface RouterTabs
{
    /**
     * Set this property to allow the Router to manage tab changes dynamically.
     * Use cases:
     * - When a tab has no pushed screens, it automatically changes the selected tab to the first one upon a physical back button click.
     * - When you use a singleton screen and it's placed in a tab, the Router will automatically switch to the corresponding tab.
     *
     * IMPORTANT: Do not set it in the Fragment's `init` method or as a `lazy var`. You should set it in the `onViewCreate`
     * or `onViewCreated` methods.
     */
    var tabChangeCallback: OnTabChangeCallback?

    /**
     * Creates a host view, binds a router to it, and sets the view specified by `path` as its root screen.
     * It's important to create all screens at adapter creation time to ensure proper functionality, especially for singleton screens.
     *
     * @param index The index of the screen in the adapter.
     * @param path  The path to the screen connected by the `RouteController`.
     */
    fun route(index: Int, path: RoutePath, recreate: Boolean): String

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
}