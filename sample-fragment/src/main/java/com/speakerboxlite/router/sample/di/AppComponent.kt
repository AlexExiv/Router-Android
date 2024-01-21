package com.speakerboxlite.router.sample.di

import com.speakerboxlite.router.sample.auth.AuthViewModel
import com.speakerboxlite.router.sample.base.middlewares.MiddlewareControllerAuth
import com.speakerboxlite.router.sample.base.middlewares.MiddlewareControllerPro
import com.speakerboxlite.router.sample.chain.ChainViewModel
import com.speakerboxlite.router.sample.chain.sub.SubChainViewModel
import com.speakerboxlite.router.sample.di.modules.AppModule
import com.speakerboxlite.router.sample.di.modules.UserModule
import com.speakerboxlite.router.sample.dialogs.DialogViewModel
import com.speakerboxlite.router.sample.main.MainViewModel
import com.speakerboxlite.router.sample.pro.ProViewModel
import com.speakerboxlite.router.sample.simple.component.SimpleComponentFragment
import com.speakerboxlite.router.sample.step.StepViewModel
import com.speakerboxlite.router.sample.step.replace.ReplaceViewModel
import com.speakerboxlite.router.sample.tabs.TabsViewModel
import com.speakerboxlite.router.sample.tabs.tab.TabViewModel
import com.speakerboxlite.router.sample.theme.ThemeViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, UserModule::class])
interface AppComponent: com.speakerboxlite.router.controllers.Component
{
    fun inject(mw: MiddlewareControllerAuth)
    fun inject(mw: MiddlewareControllerPro)

    fun inject(vm: MainViewModel)

    fun inject(vm: AuthViewModel)
    fun inject(vm: ProViewModel)

    fun inject(vm: StepViewModel)
    fun inject(vm: ReplaceViewModel)

    fun inject(vm: TabsViewModel)
    fun inject(vm: TabViewModel)

    fun inject(vm: DialogViewModel)

    fun inject(vm: ChainViewModel)
    fun inject(vm: SubChainViewModel)

    fun inject(view: SimpleComponentFragment)

    fun inject(view: ThemeViewModel)
}