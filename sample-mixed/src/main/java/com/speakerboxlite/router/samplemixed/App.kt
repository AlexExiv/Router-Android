package com.speakerboxlite.router.samplemixed

import android.app.Application
import com.speakerboxlite.router.fragment.ActivityLifeCycle
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.fragmentcompose.ActivityLifeCycleMixed
import com.speakerboxlite.router.samplemixed.main.MainPath
import com.speakerboxlite.router.samplemixed.base.animations.AnimationControllerComposeSlide
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

        routerComponent.initialize(MainPath(), AnimationControllerComposeSlide(), component)

        lifeCycle = ActivityLifeCycleMixed(routerComponent.routerManager,
            { AndroidViewModelProvider(it.fragment) },
            { HostComposeFragment() })

        registerActivityLifecycleCallbacks(lifeCycle)
    }
}