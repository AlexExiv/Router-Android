package com.speakerboxlite.router.fragmentcompose

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.R
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.fragment.ActivityLifeCycle
import com.speakerboxlite.router.fragment.FragmentLifeCycleFactory
import com.speakerboxlite.router.fragment.FragmentModelProvider

class ActivityLifeCycleMixed(routerManager: RouterManager,
                             modelProvider: FragmentModelProvider? = null,
                             val fragmentFactory: HostFragmentComposeFactory? = null):
    ActivityLifeCycle(routerManager, modelProvider, FragmentLifeCycleFactory { FragmentLifeCycleMixed(routerManager, modelProvider, fragmentFactory) })
{
    override fun onCreateExecutor(activity: Activity): CommandExecutor? =
        if (activity is AppCompatActivity)
            CommandExecutorFragmentMixed(activity, R.id.root, activity.supportFragmentManager, activity as? HostActivityFactory, activity as? HostCloseable, fragmentFactory)
        else
            null
}