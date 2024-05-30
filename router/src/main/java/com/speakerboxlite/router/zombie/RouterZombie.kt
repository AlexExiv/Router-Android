package com.speakerboxlite.router.zombie

import android.os.Bundle
import com.speakerboxlite.router.RouterResultDispatcher
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterLocal
import com.speakerboxlite.router.RouterModelProvider
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.ViewResult
import com.speakerboxlite.router.annotations.InternalApi
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.result.RouterResultProvider
import java.util.UUID

@InternalApi
class RouterZombie : Router, RouterLocal
{
    override val key: String = UUID.randomUUID().toString()

    override var topRouter: Router? = null

    override val hasPreviousScreen: Boolean get() = false
    override var lockBack: Boolean = false

    override fun route(url: String): Router? = null

    override fun route(path: RoutePath, presentation: Presentation?): Router? = null

    override fun route(path: RouteParamsGen): Router? = null

    override fun <VR: ViewResult, R: Any> routeWithResult(viewResult: VR, path: RoutePathResult<R>, presentation: Presentation?, result: RouterResultDispatcher<VR, R>): Router? = null

    override fun replace(path: RoutePath): Router? = null

    override fun back(): Router? = null

    override fun close(): Router? = null

    override fun closeTo(key: String): Router? = null

    override fun closeToTop(): Router? = null

    override fun bindExecutor(executor: CommandExecutor)
    {
        
    }

    override fun unbindExecutor()
    {
        
    }

    override fun onPrepareView(view: View, viewModel: ViewModel?)
    {

    }

    override fun <VM : ViewModel> provideViewModel(view: View, modelProvider: RouterModelProvider): VM
    {
        TODO("Not yet implemented")
    }

    override fun onComposeAnimation(view: View)
    {

    }

    override fun createRouterLocal(key: String): RouterLocal = RouterZombie()

    override fun createRouterTabs(key: String): RouterTabs = RouterTabsZombie()

    override fun removeView(key: String)
    {
        
    }

    override fun createResultProvider(key: String): RouterResultProvider = RouterResultProviderZombie()

    override fun performSave(bundle: Bundle)
    {

    }

    override fun performRestore(bundle: Bundle)
    {

    }

    override fun routeInContainer(containerId: Int, path: RoutePath): String = ""

    override fun <VR: ViewResult, R: Any> routeInContainerWithResult(viewResult: VR, containerId: Int, path: RoutePath, result: RouterResultDispatcher<VR, R>): String = ""
}