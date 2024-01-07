package com.speakerboxlite.router.fragmentcompose

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.fragment.AnimationControllerFragment
import com.speakerboxlite.router.fragment.CommandExecutorAndroid

fun interface HostFragmentComposeFactory
{
    fun onCreate(): ComposeHostView
}

class CommandExecutorAndroidMixed(activity: FragmentActivity,
                                  @IdRes containerId: Int,
                                  fragmentManager: FragmentManager,
                                  activityFactory: HostActivityFactory? = null,
                                  hostCloseable: HostCloseable? = null,
                                  val fragmentFactory: HostFragmentComposeFactory? = null): CommandExecutorAndroid(activity, containerId, fragmentManager, activityFactory, hostCloseable)
{
    override fun changeHost(key: String, path: RoutePath?, animation: AnimationControllerFragment<RoutePath, View>?)
    {
        val host = fragmentFactory?.onCreate() ?: error("You are trying to create host fragment for compose view but haven't specified HostFragmentComposeFactory")
        host.viewKey = key
        pushFragment(path, host, animation, false)
    }
}