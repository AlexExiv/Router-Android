package com.speakerboxlite.router.sample.auth

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class AuthPath(val refPath: RouteParamsGen): RoutePath

@Route(presentation = Presentation.Modal)
abstract class AuthRouteController: RouteControllerApp<AuthPath, AuthViewModel, AuthFragment>()
{
    override fun onCreateViewModel(view: AuthFragment, path: AuthPath): AuthViewModel =
        view.getAndroidViewModel { AuthViewModel(path.refPath, it) }
}
