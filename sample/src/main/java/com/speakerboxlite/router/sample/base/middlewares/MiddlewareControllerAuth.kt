package com.speakerboxlite.router.sample.base.middlewares

import com.speakerboxlite.router.Result
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.annotations.Middleware
import com.speakerboxlite.router.controllers.MiddlewareControllerComponent
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.sample.auth.AuthPath
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.modules.UserData
import javax.inject.Inject

@Middleware
annotation class MiddlewareAuth

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