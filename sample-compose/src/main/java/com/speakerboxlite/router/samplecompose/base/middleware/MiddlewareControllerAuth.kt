package com.speakerboxlite.router.samplecompose.base.middleware

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.annotations.Middleware
import com.speakerboxlite.router.controllers.MiddlewareControllerComponent
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.samplecompose.auth.AuthPath
import com.speakerboxlite.router.samplecompose.di.AppComponent
import com.speakerboxlite.router.samplecompose.di.modules.UserData
import javax.inject.Inject

@Middleware
annotation class MiddlewareAuth

/**
 * Connects the `MiddlewareControllerAuth` controller to the `@MiddlewareAuth` annotation.
 * All calls of a route annotated with `@MiddlewareAuth` will trigger this middleware before navigation.
 */
@MiddlewareAuth
class MiddlewareControllerAuth: MiddlewareControllerComponent
{
    @Inject
    lateinit var userData: UserData

    override fun onInject(component: Any)
    {
        (component as AppComponent).inject(this)
    }

    override fun onRoute(router: Router, prev: RoutePath?, next: RouteParamsGen): Boolean
    {
        if (!userData.isLogin.value!!)
        {
            router.route(AuthPath(next))
            return true
        }

        return false
    }
}