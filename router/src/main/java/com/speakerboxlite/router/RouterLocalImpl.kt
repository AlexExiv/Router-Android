package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBuffer
import com.speakerboxlite.router.command.CommandBufferImpl
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.exceptions.RouteNotFoundException
import com.speakerboxlite.router.result.RouterResultProvider
import java.lang.ref.WeakReference
import java.util.UUID

class RouterLocalImpl(val viewKey: String, router: RouterSimple): RouterLocal
{
    val weakRouter = WeakReference(router)
    val router: RouterSimple? get() = weakRouter.get()

    override var topRouter: Router?
        get() = router?.topRouter
        set(value) { router?.topRouter = value }

    override val hasPreviousScreen: Boolean get() = router!!.hasPreviousScreen

    override var lockBack: Boolean
        get() = router!!.lockBack
        set(value) { router?.lockBack = value }

    protected val commandBuffer: CommandBuffer = CommandBufferImpl()

    override fun route(url: String): String? = router!!.route(url)

    override fun route(path: RoutePath, presentation: Presentation?): String = router!!.route(path, presentation)

    override fun <R: Any> routeWithResult(path: RoutePathResult<R>, presentation: Presentation?, result: Result<R>): String =
        router!!.routeWithResult(path, presentation, result)

    override fun replace(path: RoutePath): String = router!!.replace(path)

    override fun routeDialog(path: RoutePath)
    {
        router?.routeDialog(path)
    }

    override fun <R: Any> routeDialogWithResult(path: RoutePathResult<R>, result: Result<R>)
    {
        router?.routeDialogWithResult(path, result)
    }

    override fun routeBTS(path: RoutePath)
    {
        router?.routeBTS(path)
    }

    override fun <R : Any> routeBTSWithResult(path: RoutePathResult<R>, result: Result<R>)
    {
        router?.routeBTSWithResult(path, result)
    }

    override fun back()
    {
        router?.back()
    }

    override fun close()
    {
        router?.close()
    }

    override fun closeTo(key: String)
    {
        router?.closeTo(key)
    }

    override fun closeToTop()
    {
        router?.closeToTop()
    }

    override fun bindExecutor(executor: CommandExecutor)
    {
        commandBuffer.bind(executor)
    }

    override fun unbindExecutor()
    {
        commandBuffer.unbind()
    }

    override fun onComposeView(view: View)
    {
        router?.onComposeView(view)
    }

    override fun createRouterLocal(key: String): RouterLocal = router!!.createRouterLocal(key)

    override fun createRouterTabs(factory: HostViewFactory, presentInTab: Boolean): RouterTabs
    {
        TODO("Not yet implemented")
    }

    override fun removeView(key: String)
    {
        router?.removeView(key)
    }

    override fun createResultProvider(key: String): RouterResultProvider = router!!.createResultProvider(key)

    override fun routeInContainer(containerId: Int, path: RoutePath): String
    {
        val router = router ?: return ""
        val route = router.findRoute(path) ?: throw RouteNotFoundException(path)
        val view = route.onCreateView(path)
        view.viewKey = UUID.randomUUID().toString()
        router.setPath(view.viewKey, path)
        router.bindRouter(view)

        if (router is RouterInjector)
            router.connectComponent(viewKey, view.viewKey)

        commandBuffer.apply(Command.SubFragment(containerId, view))

        return view.viewKey
    }

    override fun <R: Any> routeInContainerWithResult(containerId: Int, path: RoutePath, result: Result<R>): String
    {
        val key = routeInContainer(containerId, path)
        router?.bindResult(key, viewKey) { result(it as R) }
        return key
    }
}