package com.speakerboxlite.router.samplehilt.base.middlewares

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.annotations.Middleware
import com.speakerboxlite.router.controllers.MiddlewareControllerComponent
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.samplehilt.auth.AuthPath
import com.speakerboxlite.router.samplehilt.di.AppComponent
import com.speakerboxlite.router.samplehilt.di.modules.UserData

@Middleware
annotation class MiddlewareAuth

/**
 * Connects the `MiddlewareControllerAuth` controller to the `@MiddlewareAuth` annotation.
 * All calls of a route annotated with `@MiddlewareAuth` will trigger this middleware before navigation.
 */
@MiddlewareAuth
class MiddlewareControllerAuth: MiddlewareControllerComponent
{
    lateinit var userData: UserData

    override fun onInject(component: Any)
    {
        userData = (component as AppComponent).provideUserData()
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