package com.speakerboxlite.router.command

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.speakerboxlite.router.ComposeHostView
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.HostFragmentComposeFactory
/*
class CommandExecutorAndroidMixed(activity: FragmentActivity,
                                  @IdRes containerId: Int,
                                  fragmentManager: FragmentManager,
                                  activityFactory: HostActivityFactory? = null,
                                  hostCloseable: HostCloseable? = null,
                                  val fragmentFactory: HostFragmentComposeFactory? = null): com.speakerboxlite.fragment.CommandExecutorAndroid(activity, containerId, fragmentManager, activityFactory, hostCloseable)
{
    override fun changeHost(key: String)
    {
        val host = fragmentFactory?.createHostFragment() ?: error("You are trying to screate host fragment for compose view but haven't specified HostFragmentComposeFactory")
        if (host is ComposeHostView)
            host.viewKey = key

        val transaction = fragmentManager.beginTransaction()

        transaction
            .replace(containerId, host, key)
            .addToBackStack(key)
            .commit()

        fragmentManager.executePendingTransactions()
    }
}*/