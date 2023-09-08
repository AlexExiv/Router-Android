package com.speakerboxlite.router

import com.speakerboxlite.router.pattern.UrlMatcher
import com.speakerboxlite.router.annotations.Presentation
import java.net.URL

abstract class RouteControllerBase<Path: RoutePath, VM: ViewModel, V: View<VM>, Component>: RouteController<VM>
{
    lateinit var pathClass: Class<Path>
    var pattern: UrlMatcher? = null
    override var singleTop: Boolean = false
    override var preferredPresentation: Presentation = Presentation.Push

    override fun check(url: String): Boolean
    {
        val _url = URL(url)
        return _url.host == "" || _url.host == "speakerboxlite.com" || _url.host == "www.speakerboxlite.com" || pattern?.matches(url) ?: false
    }

    override final fun check(path: RoutePath): Boolean = pathClass.isInstance(path)

    override fun convert(url: String): RoutePath
    {
        TODO("Not yet implemented")
    }

    override fun <CommonComponent> onComposeView(view: View<*>, path: RoutePath, commonComponent: CommonComponent)
    {
        val vm = onCreateViewModel(view as V, path as Path)
        view.viewModel = vm
        val component = onCreateInjector(view, vm, commonComponent)
        onInject(view, vm, component)
    }

    abstract override fun onCreateView(): V

    abstract protected fun onCreateViewModel(view: V, path: Path): VM

    protected open fun <CommonComponent> onCreateInjector(view: V, vm: VM, component: CommonComponent): Component = component as Component

    abstract protected fun onInject(view: V, vm: VM, component: Component)
}