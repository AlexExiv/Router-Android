package com.speakerboxlite.router.fragmentcompose

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.compose.ViewCompose
import com.speakerboxlite.router.fragment.AnimationControllerFragment
import com.speakerboxlite.router.fragment.CommandExecutorFragment

interface HostFragmentComposeFactory
{
    fun onCreateComposeHostView(): ComposeHostView
    fun onCreateAnimation(): AnimationControllerFragment<RoutePath, View>?
}

class CommandExecutorFragmentMixed(activity: FragmentActivity,
                                   @IdRes containerId: Int,
                                   fragmentManager: FragmentManager,
                                   activityFactory: HostActivityFactory? = null,
                                   hostCloseable: HostCloseable? = null,
                                   val fragmentFactory: HostFragmentComposeFactory? = null): CommandExecutorFragment(activity, containerId, fragmentManager, activityFactory, hostCloseable)
{
    override fun pushFragment(path: RoutePath?, view: View, animation: AnimationControllerFragment<RoutePath, View>?, replacing: Boolean)
    {
        if (view is ViewCompose)
        {
            val host = fragmentFactory?.onCreateComposeHostView() ?: error("You are trying to create host fragment for compose view but haven't specified HostFragmentComposeFactory")
            host.viewKey = view.viewKey
            pushFragment(path, host, fragmentFactory.onCreateAnimation(), replacing)
        }
        else
            super.pushFragment(path, view, animation, replacing)
    }

    override fun replaceFragment(path: RoutePath, byView: View, animation: AnimationControllerFragment<RoutePath, View>?)
    {
        if (byView is ViewCompose)
        {
            fragmentManager.popBackStack()
            pushFragment(path, byView, null, true)
        }
        else
            super.replaceFragment(path, byView, animation)
    }

    override fun showBottomSheet(view: View)
    {
        if (view is ViewCompose)
            addHost(view)
        else
            super.showBottomSheet(view)
    }

    override fun showDialog(view: View)
    {
        if (view is ViewCompose)
            addHost(view)
        else
            super.showDialog(view)
    }

    private fun addHost(view: View)
    {
        val host = fragmentFactory?.onCreateComposeHostView() ?: error("You are trying to create host fragment for compose view but haven't specified HostFragmentComposeFactory")
        host.viewKey = view.viewKey

        host as Fragment
        fragmentManager
            .beginTransaction()
            .add(containerId, host, host.viewKey)
            .addToBackStack(host.viewKey)
            .commit()

        fragmentManager.executePendingTransactions()
    }
}