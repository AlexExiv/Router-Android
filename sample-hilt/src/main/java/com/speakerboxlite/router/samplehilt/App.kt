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
    lateinit var component: AppComponent // Shared component for the Middleware controllers
    val routerComponent = RouterComponentImpl()
    lateinit var lifeCycle: ActivityLifeCycle

    override fun onCreate()
    {
        super.onCreate()

        // Create it like the Hilt's documentation says
        component = EntryPoints.get(this, AppComponent::class.java)

        routerComponent.initialize(MainPath(), { _, _ -> AnimationControllerComposeSlide() }, component)

        lifeCycle = ActivityLifeCycle(routerComponent.routerManager)
        registerActivityLifecycleCallbacks(lifeCycle)
    }
}