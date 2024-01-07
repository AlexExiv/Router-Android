package com.speakerboxlite.router.samplecompose.di

import com.speakerboxlite.router.samplecompose.di.modules.AppModule
import com.speakerboxlite.router.samplecompose.di.modules.UserModule
import com.speakerboxlite.router.samplecompose.step.StepViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, UserModule::class])
interface AppComponent: com.speakerboxlite.router.controllers.Component
{
    fun inject(vm: StepViewModel)
}