package com.speakerboxlite.router

import com.speakerboxlite.router.pattern.UrlMatcher
import com.speakerboxlite.router.annotations.Presentation
import kotlin.reflect.KClass

abstract class RouteControllerBase<Path: RoutePath, VM: ViewModel, V: View<VM>, Component>: RouteController<Path, VM>
{
    lateinit var pathClass: KClass<Path>
    var pattern: UrlMatcher? = null

    override var singleton: Boolean = false
    override var creatingInjector: Boolean = false
    override var preferredPresentation: Presentation = Presentation.Push

    open val chainPaths: List<KClass<*>> get() = listOf()
    override val isChain: Boolean get() = chainPaths.isNotEmpty()

    override fun check(url: String): Boolean = pattern?.matches(url) ?: false

    override final fun check(path: RoutePath): Boolean = pathClass.isInstance(path)

    override final fun convert(url: String): RoutePath
    {
        val match = pattern!!.match(url)!!

        val query: Map<String, String>
        val comp = url.split("?")
        if (comp.size > 1)
        {
            query = mutableMapOf()
            val pairs = comp[1].split("&")
            pairs.forEach {
                val pair = it.split("=")
                if (pair.size > 1)
                    query[pair[0]] = pair[1]
                else
                    query[pair[0]] = ""
            }
        }
        else
        {
            query = mapOf()
        }

        return convert(match.parameters, query)
    }

    open fun convert(path: Map<String, String>, query: Map<String, String>): RoutePath
    {
        TODO("")
    }

    override fun isPartOfChain(clazz: KClass<*>): Boolean = chainPaths.indexOfFirst { it == clazz } != -1

    override fun onComposeView(view: View<*>, path: RoutePath, component: Any)
    {
        val vm = onCreateViewModel(view as V, path as Path)
        view.viewModel = vm
        onInject(view, vm, component as Component)
    }

    abstract override fun onCreateView(): V

    abstract protected fun onCreateViewModel(view: V, path: Path): VM

    override fun onCreateInjector(path: Path, component: Any): Any = component

    abstract protected fun onInject(view: V, vm: VM, component: Component)
}