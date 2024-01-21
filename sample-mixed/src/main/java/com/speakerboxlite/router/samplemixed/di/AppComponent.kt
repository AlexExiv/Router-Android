package com.speakerboxlite.router.samplemixed.di

import com.speakerboxlite.router.samplemixed.di.modules.AppModule
import com.speakerboxlite.router.samplemixed.di.modules.UserModule
import com.speakerboxlite.router.samplemixed.dialog.fragment.DialogViewModel
import com.speakerboxlite.router.samplemixed.main.MainViewModel
import com.speakerboxlite.router.samplemixed.step.StepViewModel
import com.speakerboxlite.router.samplemixed.tabs.TabsViewModel
import com.speakerboxlite.router.samplemixed.tabs.tab.TabViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, UserModule::class])
interface AppComponent: com.speakerboxlite.router.controllers.Component
{
    fun inject(vm: MainViewModel)
    fun inject(vm: StepViewModel)
    fun inject(vm: TabsViewModel)
    fun inject(vm: TabViewModel)
    fun inject(vm: DialogViewModel)
}