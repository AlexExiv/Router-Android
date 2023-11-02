package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.View
import kotlin.reflect.KClass

abstract class RouteControllerC<Path: RoutePath, V: View, C: Component>: RouteController<Path, V>(), RouteControllerComponent<Path, V, C>
{
    override lateinit var componentClass: KClass<C>

    override fun onComposeView(router: Router, view: V, path: Path, component: Any)
    {
        onInject(view, component as C)
    }

    override fun onCreateInjector(path: Path, component: Any): Any = component

    abstract protected fun onInject(view: V, component: C)
}