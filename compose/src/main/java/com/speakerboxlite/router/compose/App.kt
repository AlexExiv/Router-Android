package com.speakerboxlite.router.compose

import android.app.Application
import com.speakerboxlite.router.compose.main.MainPath
import com.speakerboxlite.router.lifecycle.ActivityLifeCycle

class App: Application()
{
    val routerComponent = RouterComponentImpl()
    lateinit var lifeCycle: ActivityLifeCycle

    override fun onCreate()
    {
        super.onCreate()

        routerComponent.initialize(MainPath(), null)

        lifeCycle = ActivityLifeCycle(routerComponent.routerManager)
        registerActivityLifecycleCallbacks(lifeCycle)
    }
}