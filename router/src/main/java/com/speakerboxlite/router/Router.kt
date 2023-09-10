package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation

typealias Result<R> = (R) -> Unit

interface Router
{
    var topRouter: Router?

    fun route(url: String): String?

    fun route(path: RoutePath, presentation: Presentation = Presentation.Push): String
    fun <R: Any> routeWithResult(path: RoutePath, presentation: Presentation = Presentation.Push, result: Result<R>): String

    fun routeDialog(path: RoutePath)
    fun <R: Any> routeDialogWithResult(path: RoutePath, result: Result<R>)

    fun back()

    fun close()
    fun closeTo(key: String)
    fun closeToTop()

    fun bindExecutor(executor: CommandExecutor)
    fun unbindExecutor()

    fun onComposeView(view: View<*>)

    fun createRouterLocal(): RouterLocal
    fun createRouterTabs(factory: HostViewFactory): RouterTabs
}
