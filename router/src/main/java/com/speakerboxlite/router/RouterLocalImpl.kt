package com.speakerboxlite.router

import android.os.Bundle
import com.speakerboxlite.router.RouterSimple.Companion.TAG
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.command.ViewFactory
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.result.RouterResultProvider
import java.lang.ref.WeakReference
import java.util.UUID

open class RouterLocalImpl(val viewKey: String, router: RouterSimple): RouterLocal
{
    override val key: String get() = viewKey

    override val topRouter: Router? get() = router?.topRouter

    override val hasPreviousScreen: Boolean get() = router!!.hasPreviousScreen

    override var lockBack: Boolean
        get() = router!!.lockBack
        set(value) { router?.lockBack = value }

    protected val commandBuffer: CommandBuffer = CommandBufferImpl(ViewFactory(router))
    protected val routerTabsList = mutableListOf<RouterTabsLocal>()

    val weakRouter = WeakReference(router)
    val router: RouterSimple? get() = weakRouter.get()

    override fun route(url: String): Router? = router?.route(url)

    override fun route(path: RoutePath, presentation: Presentation?): Router? = router?.route(path, presentation)

    override fun route(path: RouteParamsGen): Router? = router?.route(path)

    override fun <VR: ViewResult, R: Any> routeWithResult(viewResult: VR, path: RoutePathResult<R>, presentation: Presentation?, result: RouterResultDispatcher<VR, R>): Router? =
        router!!.routeWithResult(viewResult, path, presentation, result)

    override fun replace(path: RoutePath): Router? = router?.replace(path)

    override fun back(): Router? = router?.back()

    override fun close(): Router? = router?.close()

    override fun closeTo(key: String): Router? = router?.closeTo(key)

    override fun closeToTop(): Router? = router?.closeToTop()

    override fun bindExecutor(executor: CommandExecutor)
    {
        commandBuffer.bind(executor)
    }

    override fun unbindExecutor()
    {
        commandBuffer.unbind()
    }

    override fun onPrepareView(view: View, viewModel: ViewModel?)
    {
        router?.onPrepareView(view, viewModel)
    }

    override fun <VM : ViewModel> provideViewModel(view: View, modelProvider: RouterModelProvider): VM =
        router!!.provideViewModel(view, modelProvider)

    override fun onComposeAnimation(view: View)
    {
        router?.onComposeAnimation(view)
    }

    override fun createRouterLocal(key: String): RouterLocal = router!!.createRouterLocal(key)

    override fun createRouterTabs(key: String): RouterTabs =
        RouterTabsLocal(key, router!!)
            .also { routerTabsList.add(it) }

    override fun removeView(key: String)
    {
        if (viewKey == key)
            routerTabsList.forEach { it.releaseRouters() }

        router?.removeView(key)
    }

    override fun createResultProvider(key: String): RouterResultProvider = router!!.createResultProvider(key)

    override fun performSave(bundle: Bundle)
    {
        TODO("Not yet implemented")
    }

    override fun performRestore(bundle: Bundle)
    {
        TODO("Not yet implemented")
    }

    override fun routeInContainer(containerId: Int, path: RoutePath): String
    {
        val router = router ?: return ""
        val viewKey = UUID.randomUUID().toString()
        router.setPath(viewKey, path)
        router.bindRouter(viewKey)

        if (router is RouterInjector)
            router.connectComponent(this.viewKey, viewKey)

        RouterConfigGlobal.log(TAG, "Show route in container: ${path::class}, View Key: $viewKey")
        commandBuffer.apply(Command.SubFragment(containerId, viewKey))

        return viewKey
    }

    override fun <VR: ViewResult, R: Any> routeInContainerWithResult(viewResult: VR, containerId: Int, path: RoutePath, result: RouterResultDispatcher<VR, R>): String
    {
        val key = routeInContainer(containerId, path)
        router?.bindResult(key, ViewResultData.create(viewResult, result))
        return key
    }

    internal fun routeInternal(path: RoutePath)
    {
        val router = router ?: return
        val viewKey = UUID.randomUUID().toString()
        router.setPath(viewKey, path)
        router.routerManager[viewKey] = this

        if (router is RouterInjector)
            router.connectComponent(this.viewKey, viewKey)

        RouterConfigGlobal.log(TAG, "Show route internal: ${path::class}, View Key: $viewKey")
        commandBuffer.apply(Command.Push(path, viewKey))
    }
}