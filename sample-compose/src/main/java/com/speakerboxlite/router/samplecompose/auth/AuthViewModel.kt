package com.speakerboxlite.router.samplecompose.auth

import android.app.Application
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.samplecompose.base.BaseViewModel
import com.speakerboxlite.router.samplecompose.di.modules.UserData
import javax.inject.Inject

class AuthViewModel(val refPath: RouteParamsGen, app: Application): BaseViewModel(app)
{
    @Inject
    lateinit var userData: UserData

    fun onSignIn()
    {
        userData.isLogin.value = true
        router.close()?.route(refPath)
    }
}