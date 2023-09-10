package com.speakerboxlite.router

import com.speakerboxlite.router.annotations.Presentation
import java.lang.ref.WeakReference
import java.util.UUID

class RouterLocalImpl(router: RouterSimple): RouterLocal
{
    val weakRouter = WeakReference(router)
    val router: RouterSimple? get() = weakRouter.get()

    override var topRouter: Router?
        get() = router?.topRouter
        set(value) { router?.topRouter = value }

    protected val commandBuffer: CommandBuffer = CommandBufferImpl()

    override fun route(url: String): String? = router!!.route(url)

    override fun route(path: RoutePath, presentation: Presentation): String = router!!.route(path, presentation)

    override fun <R: Any> routeWithResult(path: RoutePath, presentation: Presentation, result: Result<R>): String =
        router!!.routeWithResult(path, presentation, result)

    override fun routeDialog(path: RoutePath)
    {
        router?.routeDialog(path)
    }

    override fun <R: Any> routeDialogWithResult(path: RoutePath, result: Result<R>)
    {
        router?.routeDialogWithResult(path, result)
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

    override fun onComposeView(view: View<*>)
    {
        router?.onComposeView(view)
    }

    override fun createRouterLocal(): RouterLocal = router!!.createRouterLocal()

    override fun createRouterTabs(factory: HostViewFactory): RouterTabs
    {
        TODO("Not yet implemented")
    }

    override fun routeInContainer(containerId: Int, path: RoutePath)
    {
        val router = router ?: return
        val route = router.findRoute(path) ?: return
        val view = route.onCreateView()
        view.viewKey = UUID.randomUUID().toString()
        router.setPath(view.viewKey, path)
        router.bindRouter(view)

        commandBuffer.apply(Command.SubFragment(containerId, view))
    }
}