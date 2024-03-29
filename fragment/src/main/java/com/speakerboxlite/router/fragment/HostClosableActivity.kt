package com.speakerboxlite.router.fragment

import android.app.Activity
import com.speakerboxlite.router.HostCloseable
import java.lang.ref.WeakReference

interface IHostClosableActivity: HostCloseable
{
    fun withActivity(activity: Activity)
}

class HostClosableActivity: IHostClosableActivity
{
    var activity = WeakReference<Activity>(null)

    override fun onCloseHost()
    {
        activity.get()?.finish()
    }

    override fun withActivity(activity: Activity)
    {
        this.activity = WeakReference(activity)
    }
}