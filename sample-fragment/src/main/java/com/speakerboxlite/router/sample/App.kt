package com.speakerboxlite.router.sample

import android.app.Application
import com.speakerboxlite.router.fragment.ActivityLifeCycle
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.sample.base.animations.AnimationControllerDefault
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.DaggerAppComponent
import com.speakerboxlite.router.sample.di.modules.AppData
import com.speakerboxlite.router.sample.di.modules.AppModule
import com.speakerboxlite.router.sample.di.modules.UserData
import com.speakerboxlite.router.sample.di.modules.UserModule
import com.speakerboxlite.router.sample.main.MainPath
import com.speakerboxlite.router.samplefragment.RouterComponentImpl

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

        routerComponent.initialize(MainPath(), AnimationControllerDefault(), component)

        lifeCycle = ActivityLifeCycle(routerComponent.routerManager, { AndroidViewModelProvider(it.fragment) })
        registerActivityLifecycleCallbacks(lifeCycle)
    }
}