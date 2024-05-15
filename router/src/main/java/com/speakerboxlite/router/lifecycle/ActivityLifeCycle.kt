package com.speakerboxlite.router.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.speakerboxlite.router.BaseHostView
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.RouterManagerImpl
import com.speakerboxlite.router.START_ACTIVITY_KEY
import com.speakerboxlite.router.annotations.InternalApi
import com.speakerboxlite.router.zombie.RouterZombie
import com.speakerboxlite.router.ext.restartApp
import com.speakerboxlite.router.hostActivityKey

@OptIn(InternalApi::class)
open class ActivityLifeCycle(val routerManager: RouterManager): Application.ActivityLifecycleCallbacks
{
    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?)
    {
        super.onActivityPreCreated(activity, savedInstanceState)


    }

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

                if (!routerManager.isAppRestarting && !p0.isFinishing)
                {
                    (routerManager as? RouterManagerImpl)?.resetToTop()
                    p0.restartApp()
                }
            }
            else
                p0.router = router
        }
    }

    override fun onActivityStarted(p0: Activity)
    {

    }

    override fun onActivityResumed(p0: Activity)
    {

    }

    override fun onActivityPaused(p0: Activity)
    {

    }

    override fun onActivityStopped(p0: Activity)
    {

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
}