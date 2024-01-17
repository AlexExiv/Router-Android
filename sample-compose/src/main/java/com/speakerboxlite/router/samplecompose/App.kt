package com.speakerboxlite.router.samplecompose

import android.app.Application
import com.speakerboxlite.router.samplecompose.main.MainPath
import com.speakerboxlite.router.lifecycle.ActivityLifeCycle
import com.speakerboxlite.router.samplecompose.base.animations.AnimationControllerComposeSlide
import com.speakerboxlite.router.samplecompose.di.AppComponent
import com.speakerboxlite.router.samplecompose.di.DaggerAppComponent
import com.speakerboxlite.router.samplecompose.di.modules.AppData
import com.speakerboxlite.router.samplecompose.di.modules.AppModule
import com.speakerboxlite.router.samplecompose.di.modules.UserData
import com.speakerboxlite.router.samplecompose.di.modules.UserModule

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

        routerComponent.initialize(MainPath(), { _, _, _ -> AnimationControllerComposeSlide() }, component)

        lifeCycle = ActivityLifeCycle(routerComponent.routerManager)
        registerActivityLifecycleCallbacks(lifeCycle)
    }
}