package com.speakerboxlite.router.samplemixed

import android.util.Log
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.RouterConfigGlobal
import com.speakerboxlite.router.View
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.controllers.AnimationController
import com.speakerboxlite.router.controllers.AnimationControllerFactory
import com.speakerboxlite.router.fragment.AnimationControllerFragment
import com.speakerboxlite.router.fragment.ViewFragment
import com.speakerboxlite.router.fragmentcompose.ComposeHostView
import com.speakerboxlite.router.fragmentcompose.HostFragmentComposeFactory
import com.speakerboxlite.router.fragmentcompose.bootstrap.MixedApplication
import com.speakerboxlite.router.samplemixed.main.MainPath
import com.speakerboxlite.router.samplemixed.base.animations.AnimationControllerComposeSlide
import com.speakerboxlite.router.samplemixed.base.animations.AnimationControllerFragmentDefault
import com.speakerboxlite.router.samplemixed.base.fragment.HostComposeFragment
import com.speakerboxlite.router.samplemixed.di.AppComponent
import com.speakerboxlite.router.samplemixed.di.DaggerAppComponent
import com.speakerboxlite.router.samplemixed.di.modules.AppData
import com.speakerboxlite.router.samplemixed.di.modules.AppModule
import com.speakerboxlite.router.samplemixed.di.modules.UserData
import com.speakerboxlite.router.samplemixed.di.modules.UserModule

class App: MixedApplication<RouterComponentImpl>()
{
    lateinit var component: AppComponent

    init
    {
        RouterConfigGlobal.restoreSingleTime = false
        RouterConfigGlobal.logFun = { t, m -> Log.d(t, m) }
    }

    override fun onCreateComponent()
    {
        super.onCreateComponent()

        component = DaggerAppComponent.builder()
            .appModule(AppModule(AppData("App String")))
            .userModule(UserModule(UserData()))
            .build()
    }

    override fun onCreateRouter()
    {
        routerComponent = RouterComponentImpl()
        routerComponent.initialize(MainPath(), AnimationFactory(), component)
    }

    override fun provideHostFragmentComposeFactory(): HostFragmentComposeFactory =
        HostFragmentComposeFactoryImpl()
}

class HostFragmentComposeFactoryImpl: HostFragmentComposeFactory
{
    override fun onCreateComposeHostView(): ComposeHostView =
        HostComposeFragment()

    override fun onCreateAnimation(): AnimationControllerFragment<RoutePath, View>? =
        AnimationControllerFragmentDefault()
}

class AnimationFactory: AnimationControllerFactory
{
    override fun onCreate(presentation: Presentation?, view: View): AnimationController?
    {
        return if (view is ViewFragment)
            AnimationControllerFragmentDefault()
        else
            AnimationControllerComposeSlide()
    }
}
