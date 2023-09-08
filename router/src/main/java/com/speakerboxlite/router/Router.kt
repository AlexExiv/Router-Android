package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation

typealias Result<R> = (R) -> Unit

interface HostViewFactory
{
    fun create(): HostView
}

interface Router
{
    var topRouter: Router?

    fun route(url: String): String?

    fun route(path: RoutePath, presentation: Presentation = Presentation.Push): String
    fun <R: Any> routeWithResult(path: RoutePath, presentation: Presentation = Presentation.Push, result: Result<R>): String

    fun subRoute(path: RoutePath, presentation: Presentation = Presentation.Push): String
    fun <R: Any> subRouteWithResult(path: RoutePath, presentation: Presentation = Presentation.Push, result: Result<R>): String

    fun chainRoute(path: RoutePath, presentation: Presentation = Presentation.Push): String
    fun <R: Any> chainRouteWithResult(path: RoutePath, presentation: Presentation = Presentation.Push, result: Result<R>): String

    fun routeDialog(path: RoutePath)
    fun <R: Any> routeDialogWithResult(path: RoutePath, result: Result<R>)

    fun <R: Any> sendResult(result: R)

    fun back()

    fun close()
    fun <R: Any> closeWithResult(result: R)

    fun closeTo(key: String)
    fun closeToTop()

    fun bindExecutor(executor: CommandExecutor)
    fun unbindExecutor()

    fun onComposeView(view: View<*>)

    fun createRouterLocal(): RouterLocal
    fun createRouterTabs(factory: HostViewFactory): RouterTabs
}
