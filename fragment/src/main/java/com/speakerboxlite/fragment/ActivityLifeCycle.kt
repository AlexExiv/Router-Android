package com.speakerboxlite.fragment

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.speakerboxlite.router.BaseHostView
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.R
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.RouterManagerImpl
import com.speakerboxlite.router.START_ACTIVITY_KEY
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.hostActivityKey

open class ActivityLifeCycle(routerManager: RouterManager,
                             private val modelProvider: FragmentModelProvider? = null): com.speakerboxlite.router.lifecycle.ActivityLifeCycle(routerManager)
{
    private val routerByActivity = mutableMapOf<Router, Activity>()

    override fun onActivityCreated(p0: Activity, p1: Bundle?)
    {
        super.onActivityCreated(p0, p1)

        if (p0 is AppCompatActivity)
        {
            p0.supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifeCycle(routerManager, modelProvider), true)
        }
    }

    override fun onActivityStarted(p0: Activity)
    {
        super.onActivityStarted(p0)

        if (p0 is BaseHostView && p0 is AppCompatActivity)
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

    override fun onActivityStopped(p0: Activity)
    {
        super.onActivityStopped(p0)

        if (p0 is BaseHostView && routerByActivity[p0.router] == p0)
        {
            p0.router.unbindExecutor()
            routerByActivity.remove(p0.router)
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle)
    {
        super.onActivitySaveInstanceState(p0, p1)

        if (p0.hostActivityKey == START_ACTIVITY_KEY)//stop the app restarting process if this is a START_ACTIVITY
            (routerManager as? RouterManagerImpl)?.isAppRestarting = false
    }

    override fun onActivityDestroyed(p0: Activity)
    {
        super.onActivityDestroyed(p0)

        if (p0 is BaseHostView && p0.isFinishing)
            routerManager[p0.hostActivityKey] = null
    }

    protected open fun onCreateExecutor(activity: Activity): CommandExecutor? =
        if (activity is AppCompatActivity)
            CommandExecutorAndroid(activity, R.id.root, activity.supportFragmentManager, activity as? HostActivityFactory, activity as? HostCloseable)
        else
            null
}