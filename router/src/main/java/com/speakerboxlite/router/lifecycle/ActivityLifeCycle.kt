package com.speakerboxlite.router.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.R
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.RouterManagerImpl
import com.speakerboxlite.router.START_ACTIVITY_KEY
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.zombie.RouterZombie
import com.speakerboxlite.router.command.CommandExecutorAndroid
import com.speakerboxlite.router.ext.restartApp
import com.speakerboxlite.router.hostActivityKey

interface BaseHostView
{
    var routerManager: RouterManager
    var router: Router
}

open class ActivityLifeCycle(val routerManager: RouterManager): Application.ActivityLifecycleCallbacks
{
    private val routerByActivity = mutableMapOf<Router, Activity>()

    override fun onActivityCreated(p0: Activity, p1: Bundle?)
    {
        if (p0 is BaseHostView)
        {
            p0.routerManager = routerManager
            val router = routerManager[p0.hostActivityKey]

            //In case of we couldn't find the router start the restarting process. It may occur after the app restores the state after the reboot maybe better
            //solution is to serialize routers.
            if (router == null)
            {
                p0.router = RouterZombie()

                if (!routerManager.isAppRestarting)
                {
                    (routerManager as? RouterManagerImpl)?.resetToTop()
                    p0.restartApp()
                }
            }
            else
                p0.router = router
        }

        if (p0 is AppCompatActivity)
        {
            p0.supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifeCycle(routerManager), true)
        }
    }

    override fun onActivityStarted(p0: Activity)
    {
        if (p0 is BaseHostView)
        {
            if (p0 is AppCompatActivity)
            {
                if (routerByActivity[p0.router] != null)
                {
                    p0.router.unbindExecutor()
                    routerByActivity.remove(p0.router)
                }

                routerByActivity[p0.router] = p0

                val executor = onCreateExecutor(p0)
                if (executor != null)
                    p0.router.bindExecutor(executor)
            }
        }
    }

    override fun onActivityResumed(p0: Activity)
    {
        if (p0 is BaseHostView)
        {
            //routerManager.top = p0.router
        }
    }

    override fun onActivityPaused(p0: Activity)
    {

    }

    override fun onActivityStopped(p0: Activity)
    {
        if (p0 is BaseHostView)
        {
            if (routerByActivity[p0.router] == p0)
            {
                p0.router.unbindExecutor()
                routerByActivity.remove(p0.router)
            }
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle)
    {
        if (p0.hostActivityKey == START_ACTIVITY_KEY)//stop the app restarting process if this is a START_ACTIVITY
            (routerManager as? RouterManagerImpl)?.isAppRestarting = false
    }

    override fun onActivityDestroyed(p0: Activity)
    {
        if (p0 is BaseHostView && p0.isFinishing)
            routerManager[p0.hostActivityKey] = null
    }

    protected open fun onCreateExecutor(activity: Activity): CommandExecutor? =
        if (activity is AppCompatActivity)
            CommandExecutorAndroid(activity, R.id.root, activity.supportFragmentManager, activity as? HostActivityFactory, activity as? HostCloseable)
        else
            null
}