package com.speakerboxlite.router.sample.base.middlewares

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.annotations.Middleware
import com.speakerboxlite.router.controllers.MiddlewareControllerComponent
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.modules.UserData
import com.speakerboxlite.router.sample.pro.ProPath
import javax.inject.Inject

@Middleware
annotation class MiddlewarePro

/**
 * Connects the `MiddlewareControllerPro` controller to the `@MiddlewarePro` annotation.
 * All calls of a route annotated with `@MiddlewarePro` will trigger this middleware before navigation.
 */
@MiddlewarePro
class MiddlewareControllerPro: MiddlewareControllerComponent
{
    @Inject
    lateinit var userData: UserData

    override fun onInject(component: Any)
    {
        (component as AppComponent).inject(this)
    }

    override fun onRoute(router: Router, prev: RoutePath?, next: RouteParamsGen): Boolean
    {
        if (!userData.isPro.value!!)
        {
            router.route(ProPath(next))
            return true
        }

        return false
    }
}