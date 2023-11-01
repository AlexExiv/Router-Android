package com.speakerboxlite.router.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.speakerboxlite.router.command.CommandExecutorAndroid
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.R
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.RouterSimple
import com.speakerboxlite.router.exceptions.RouterNotFoundException
import com.speakerboxlite.router.hostActivityKey

interface BaseHostView
{
    var routerManager: RouterManager
    var router: Router
}

class ActivityLifeCycle(val routerManager: RouterManager,
                        val hostActivityFactory: HostActivityFactory): Application.ActivityLifecycleCallbacks
{
    override fun onActivityCreated(p0: Activity, p1: Bundle?)
    {
        if (p0 is BaseHostView)
        {
            p0.routerManager = routerManager
            p0.router = routerManager[p0.hostActivityKey] ?: throw RouterNotFoundException(p0, routerManager, p1)
        }

        if (p0 is AppCompatActivity)
        {
            p0.supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifeCycle(routerManager, hostActivityFactory), true)
        }
    }

    override fun onActivityStarted(p0: Activity)
    {
        if (p0 is BaseHostView)
        {
            if (p0 is AppCompatActivity)
                p0.router.bindExecutor(CommandExecutorAndroid(p0, R.id.root, p0.supportFragmentManager, hostActivityFactory))
        }
    }

    override fun onActivityResumed(p0: Activity)
    {
        if (p0 is BaseHostView)
        {
            routerManager.top = p0.router
        }
    }

    override fun onActivityPaused(p0: Activity)
    {

    }

    override fun onActivityStopped(p0: Activity)
    {
        if (p0 is BaseHostView)
        {
            p0.router.unbindExecutor()
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle)
    {

    }

    override fun onActivityDestroyed(p0: Activity)
    {
        if (p0 is BaseHostView && p0.isFinishing)
            routerManager[p0.hostActivityKey] = null
    }
}