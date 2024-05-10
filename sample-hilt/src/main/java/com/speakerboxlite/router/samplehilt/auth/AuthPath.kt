package com.speakerboxlite.router.samplehilt.auth

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.androidhilt.AndroidHiltViewModelProvider
import com.speakerboxlite.router.samplehilt.base.RouteControllerApp

data class AuthPath(val refPath: RouteParamsGen): RoutePath

@Route
abstract class AuthRouteController: RouteControllerApp<AuthPath, AuthViewModel, AuthView>()
{
    override fun onCreateViewModel(modelProvider: AndroidHiltViewModelProvider, path: AuthPath): AuthViewModel =
        modelProvider.getViewModel { f: AuthViewModel.Factory -> f.create(path.refPath) }
}
