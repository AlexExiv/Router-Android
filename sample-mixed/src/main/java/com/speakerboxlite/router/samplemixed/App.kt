package com.speakerboxlite.router.samplemixed

import android.app.Application
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.controllers.AnimationController
import com.speakerboxlite.router.controllers.AnimationControllerFactory
import com.speakerboxlite.router.controllers.AnimationHostChanged
import com.speakerboxlite.router.fragment.ActivityLifeCycle
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.fragment.AnimationControllerFragment
import com.speakerboxlite.router.fragment.ViewFragment
import com.speakerboxlite.router.fragmentcompose.ActivityLifeCycleMixed
import com.speakerboxlite.router.fragmentcompose.ComposeHostView
import com.speakerboxlite.router.fragmentcompose.HostFragmentComposeFactory
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

class App: Application()
{
    lateinit var component: AppComponent
    val routerComponent = RouterComponentImpl()
    lateinit var lifeCycle: ActivityLifeCycle

    override fun onCreate()
    {
        super.onCreate()

        component = DaggerAppComponent.builder()
            .appModule(AppModule(AppData("App String")))
            .userModule(UserModule(UserData()))
            .build()

        routerComponent.initialize(MainPath(), AnimationFactory(), component)

        lifeCycle = ActivityLifeCycleMixed(routerComponent.routerManager,
            { AndroidViewModelProvider(it.fragment) },
            HostFragmentComposeFactoryImpl())

        registerActivityLifecycleCallbacks(lifeCycle)
    }
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
    override fun onCreate(presentation: Presentation?, view: View?, hostChanged: AnimationHostChanged?): AnimationController?
    {
        return if (hostChanged != null)
        {
            when (hostChanged)
            {
                AnimationHostChanged.FromFragment -> AnimationControllerFragmentDefault()
                AnimationHostChanged.FromCompose -> AnimationControllerComposeSlide()
            }
        }
        else if (view != null)
        {
            if (view is ViewFragment)
                AnimationControllerFragmentDefault()
            else
                AnimationControllerComposeSlide()
        }
        else
            null
    }
}
