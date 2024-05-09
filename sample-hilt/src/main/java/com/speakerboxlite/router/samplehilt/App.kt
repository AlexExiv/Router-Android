package com.speakerboxlite.router.samplehilt

import android.app.Application
import com.speakerboxlite.router.lifecycle.ActivityLifeCycle
import com.speakerboxlite.router.samplehilt.base.animations.AnimationControllerComposeSlide
import com.speakerboxlite.router.samplehilt.di.AppComponent
import com.speakerboxlite.router.samplehilt.main.MainPath
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application()
{
    lateinit var component: AppComponent
    val routerComponent = RouterComponentImpl()
    lateinit var lifeCycle: ActivityLifeCycle

    override fun onCreate()
    {
        super.onCreate()

        component = EntryPoints.get(this, AppComponent::class.java)

        routerComponent.initialize(MainPath(), { _, _ -> AnimationControllerComposeSlide() }, component)

        lifeCycle = ActivityLifeCycle(routerComponent.routerManager)
        registerActivityLifecycleCallbacks(lifeCycle)
    }
}