package com.speakerboxlite.router.samplecompose.auth

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.compose.AndroidComposeViewModelProvider
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.samplecompose.base.RouteControllerApp

data class AuthPath(val refPath: RouteParamsGen): RoutePath

@Route
abstract class AuthRouteController: RouteControllerApp<AuthPath, AuthViewModel, AuthView>()
{
    override fun onCreateViewModel(modelProvider: AndroidComposeViewModelProvider, path: AuthPath): AuthViewModel =
        modelProvider.getViewModel { AuthViewModel(path.refPath, it) }
}
