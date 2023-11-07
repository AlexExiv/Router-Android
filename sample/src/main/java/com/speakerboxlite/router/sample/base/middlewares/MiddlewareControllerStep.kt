package com.speakerboxlite.router.sample.base.middlewares

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.annotations.GlobalMiddleware
import com.speakerboxlite.router.controllers.MiddlewareController
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.sample.auth.AuthPath
import com.speakerboxlite.router.sample.dialogs.DialogPath

@GlobalMiddleware(0)
class MiddlewareControllerStep: MiddlewareController
{
    var i = 0

    override fun onRoute(router: Router, prev: RoutePath?, next: RouteParamsGen): Boolean
    {
        i += 1

        if (i % 9 == 0 && next::class != AuthPath::class)
        {
            router.routeDialog(DialogPath(message = "I'm a step middleware", okBtn = "Close"))
            return true
        }

        return false
    }
}