package com.speakerboxlite.router.zombie

import com.speakerboxlite.router.Result
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterLocal
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.View
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.result.RouterResultProvider

internal class RouterZombie : Router, RouterLocal
{
    override var topRouter: Router? = null

    override val hasPreviousScreen: Boolean get() = false
    override var lockBack: Boolean = false

    override fun route(url: String): String? = null

    override fun route(path: RoutePath, presentation: Presentation?): String = ""

    override fun <R : Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): String = ""

    override fun replace(path: RoutePath): String = ""

    override fun routeDialog(path: RoutePath)
    {
        
    }

    override fun <R : Any> routeDialogWithResult(path: RoutePathResult<R>, result: Result<R>)
    {
        
    }

    override fun routeBTS(path: RoutePath)
    {
        
    }

    override fun <R : Any> routeBTSWithResult(path: RoutePathResult<R>, result: Result<R>)
    {
        
    }

    override fun back()
    {
        
    }

    override fun close()
    {
        
    }

    override fun closeTo(key: String)
    {
        
    }

    override fun closeToTop()
    {
        
    }

    override fun bindExecutor(executor: CommandExecutor)
    {
        
    }

    override fun unbindExecutor()
    {
        
    }

    override fun onComposeView(view: View)
    {
        
    }

    override fun onComposeAnimation(view: View)
    {
        view.resultProvider = createResultProvider("")
    }

    override fun createRouterLocal(key: String): RouterLocal = RouterZombie()

    override fun createRouterTabs(key: String, presentInTab: Boolean): RouterTabs = RouterTabsZombie()

    override fun removeView(key: String)
    {
        
    }

    override fun createResultProvider(key: String): RouterResultProvider = RouterResultProviderZombie()

    override fun routeInContainer(containerId: Int, path: RoutePath): String = ""

    override fun <R : Any> routeInContainerWithResult(containerId: Int, path: RoutePath, result: Result<R>): String = ""
}