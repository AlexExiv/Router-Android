package com.speakerboxlite.router.samplehilt.auth

import android.app.Application
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.samplehilt.base.BaseViewModel
import com.speakerboxlite.router.samplehilt.di.modules.UserData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel(assistedFactory = AuthViewModel.Factory::class)
class AuthViewModel @AssistedInject constructor(
    private val userData: UserData,
    @Assisted val refPath: RouteParamsGen,
    app: Application): BaseViewModel(app)
{
    @AssistedFactory
    interface Factory
    {
        fun create(refPath: RouteParamsGen): AuthViewModel
    }

    fun onSignIn()
    {
        userData.isLogin.value = true
        router.close()?.route(refPath)
    }
}