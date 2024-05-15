package com.speakerboxlite.router.fragment.bootstrap

import android.app.Application
import com.speakerboxlite.router.RouterComponent
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.lifecycle.ActivityLifeCycle

abstract class FragmentApplication<RC: RouterComponent>: Application()
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

        lifeCycle = com.speakerboxlite.router.fragment.ActivityLifeCycle(routerComponent.routerManager, { AndroidViewModelProvider(it.fragment) })
        registerActivityLifecycleCallbacks(lifeCycle)
    }

    /**
     * Override it if you need to create a component for injection
     */
    open fun onCreateComponent() {}
    abstract fun onCreateRouter()
}