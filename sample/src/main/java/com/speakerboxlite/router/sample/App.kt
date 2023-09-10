package com.speakerboxlite.router.sample

import android.app.Application
import android.content.Intent
import com.speakerboxlite.router.lifecycle.ActivityLifeCycle
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.RouterComponent
import com.speakerboxlite.router.sample.base.HostActivity
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.DaggerAppComponent
import com.speakerboxlite.router.sample.main.MainPath

class App: Application(), HostActivityFactory
{
    lateinit var component: AppComponent
    val routerComponent: RouterComponent = RouterComponentImpl()
    lateinit var lifeCycle: ActivityLifeCycle

    override fun onCreate()
    {
        super.onCreate()

        component = DaggerAppComponent.builder()
            .build()

        routerComponent.initialize(component, MainPath())

        lifeCycle = ActivityLifeCycle(routerComponent.routerManager, this)
        registerActivityLifecycleCallbacks(lifeCycle)

    }

    override fun create(): Intent = Intent(this, HostActivity::class.java)
}