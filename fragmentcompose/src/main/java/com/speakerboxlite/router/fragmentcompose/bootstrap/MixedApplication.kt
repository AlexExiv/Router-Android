package com.speakerboxlite.router.fragmentcompose.bootstrap

import android.app.Application
import com.speakerboxlite.router.RouterComponent
import com.speakerboxlite.router.fragment.ActivityLifeCycle
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.fragmentcompose.ActivityLifeCycleMixed
import com.speakerboxlite.router.fragmentcompose.HostFragmentComposeFactory

abstract class MixedApplication<RC: RouterComponent>: Application()
{
    lateinit var routerComponent: RC
    lateinit var lifeCycle: ActivityLifeCycle

    override fun onCreate()
    {
        super.onCreate()

        onCreateComponent()
        onCreateRouter()
        if (!::routerComponent.isInitialized)
            error("You didn't call routerComponent = RouterComponentImpl() in the onInitRouter method")

        lifeCycle = ActivityLifeCycleMixed(
            routerComponent.routerManager,
            { AndroidViewModelProvider(it.fragment) },
            provideHostFragmentComposeFactory())

        registerActivityLifecycleCallbacks(lifeCycle)
    }

    /**
     * Override it if you need to create a component for injection
     */
    open fun onCreateComponent() {}
    abstract fun onCreateRouter()

    abstract fun provideHostFragmentComposeFactory(): HostFragmentComposeFactory
}