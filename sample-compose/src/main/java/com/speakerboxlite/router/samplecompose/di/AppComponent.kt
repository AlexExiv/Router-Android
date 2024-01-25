package com.speakerboxlite.router.samplecompose.di

import com.speakerboxlite.router.samplecompose.auth.AuthViewModel
import com.speakerboxlite.router.samplecompose.base.middleware.MiddlewareControllerAuth
import com.speakerboxlite.router.samplecompose.di.modules.AppModule
import com.speakerboxlite.router.samplecompose.di.modules.UserModule
import com.speakerboxlite.router.samplecompose.main.MainViewModel
import com.speakerboxlite.router.samplecompose.result.ResultViewModel
import com.speakerboxlite.router.samplecompose.step.StepViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, UserModule::class])
interface AppComponent: com.speakerboxlite.router.controllers.Component
{
    fun inject(mw: MiddlewareControllerAuth)

    fun inject(vm: MainViewModel)
    fun inject(vm: StepViewModel)
    fun inject(vm: AuthViewModel)
    fun inject(vm: ResultViewModel)
}