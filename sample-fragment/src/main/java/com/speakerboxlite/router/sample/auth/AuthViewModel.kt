package com.speakerboxlite.router.sample.auth

import android.app.Application
import com.speakerboxlite.router.controllers.RouteParamsGen
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.di.modules.UserData
import javax.inject.Inject

class AuthViewModel(val refPath: RouteParamsGen, app: Application): BaseViewModel(app)
{
    @Inject
    lateinit var userData: UserData

    fun onAuth()
    {
        userData.isLogin.value = true

        router.close()
        router.route(refPath)
    }
}