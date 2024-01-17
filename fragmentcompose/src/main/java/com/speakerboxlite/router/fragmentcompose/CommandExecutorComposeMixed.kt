package com.speakerboxlite.router.fragmentcompose

import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.compose.AnimationControllerCompose
import com.speakerboxlite.router.compose.CommandExecutorCompose
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.fragment.AnimationControllerFragment
import com.speakerboxlite.router.fragment.ViewFragment

interface ComposeViewHoster: com.speakerboxlite.router.compose.ComposeViewHoster
{
    fun onCreateComposeFragmentHostView(): ComposeFragmentHostView
    fun onCreateAnimation(): AnimationControllerFragment<RoutePath, View>?
}

class CommandExecutorComposeMixed(navigator: ComposeNavigator,
                                  @IdRes val containerId: Int,
                                  val fragmentManager: FragmentManager,
                                  hoster: ComposeViewHoster? = null,
                                  hostCloseable: HostCloseable? = null): CommandExecutorCompose(navigator, hoster, hostCloseable)
{
    override fun push(path: RoutePath?, view: View, animationController: AnimationControllerCompose?)
    {
        if (view is ViewFragment)
        {
            val hoster = hoster as? ComposeViewHoster ?: error("")
            val transaction = fragmentManager.beginTransaction()

            val viewHost = hoster.onCreateComposeFragmentHostView()
            viewHost.viewKey = view.viewKey
            viewHost.onCloseCallback = { fragmentManager.popBackStackImmediate() }

            viewHost as Fragment

            val anim = hoster.onCreateAnimation()
            if (anim != null && path != null)
            {
                val current = fragmentManager.findFragmentById(containerId)

                transaction.setReorderingAllowed(true)
                anim.onConfigureAnimation(path, transaction, current, view, false)
            }

            transaction
                .replace(containerId, viewHost, viewHost.viewKey)
                .addToBackStack(viewHost.viewKey)
                .commit()

            fragmentManager.executePendingTransactions()
        }
        else
            super.push(path, view, animationController)
    }

    override fun replace(path: RoutePath?, view: View)
    {
        if (view is ViewFragment)
        {
            if (!navigator.isEmpty)
                navigator.pop()

            push(path, view, null)
        }
        else
            super.replace(path, view)
    }

    override fun showBottomSheet(view: View)
    {
        if (view is ViewFragment)
            addHost(view)
        else
            super.showBottomSheet(view)
    }

    override fun showDialog(view: View)
    {
        if (view is ViewFragment)
            addHost(view)
        else
            super.showDialog(view)
    }

    private fun addHost(view: View)
    {
        val hoster = hoster as? ComposeViewHoster ?: error("")
        val transaction = fragmentManager.beginTransaction()

        val viewHost = hoster.onCreateComposeFragmentHostView()
        viewHost.viewKey = view.viewKey
        viewHost.onCloseCallback = { fragmentManager.popBackStackImmediate() }

        viewHost as Fragment

        transaction
            .add(containerId, viewHost, viewHost.viewKey)
            .addToBackStack(viewHost.viewKey)
            .commit()

        fragmentManager.executePendingTransactions()
    }
}