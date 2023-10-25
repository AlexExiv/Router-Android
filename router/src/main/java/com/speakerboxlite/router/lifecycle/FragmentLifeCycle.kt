package com.speakerboxlite.router.lifecycle

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.speakerboxlite.router.command.CommandExecutorAndroid
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.R
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ext.isPoppedRecursive
import com.speakerboxlite.router.ext.isRemovingRecursive

class FragmentLifeCycle(private val routerManager: RouterManager,
                        private val hostActivityFactory: HostActivityFactory): FragmentManager.FragmentLifecycleCallbacks()
{
    private var resumedHoster = false

    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?)
    {
        if (f is HostView)
        {
            f.router = routerManager.getForView(f.viewKey)
        }

        if (f is View)
        {
            f.router = routerManager.getForView(f.viewKey)
            f.localRouter = f.router?.createRouterLocal(f.viewKey)
            f.router?.onComposeView(f)
        }
    }

    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: android.view.View, savedInstanceState: Bundle?)
    {
        if (f is View)
            f.router?.onComposeAnimation(f)
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment)
    {
        if (f is HostView && f.router != null)
        {
            if (!resumedHoster)
            {
                f.router?.topRouter = f.router
                resumedHoster = true
            }

            f.router?.bindExecutor(CommandExecutorAndroid(f.requireActivity(), R.id.root, f.childFragmentManager, hostActivityFactory))
        }

        if (f is View && f.router != null)
        {
            if (f.parentFragment == null && !resumedHoster)
                f.router?.topRouter = f.router

            f.resultProvider.start()
            f.localRouter?.bindExecutor(CommandExecutorAndroid(f.requireActivity(), R.id.root, f.childFragmentManager, hostActivityFactory))
        }
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment)
    {
        if (f is HostView)
        {
            resumedHoster = false
            f.router?.unbindExecutor()
        }

        if (f is View)
        {
            f.localRouter?.unbindExecutor()
            f.resultProvider.pause()
        }
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment)
    {

    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment)
    {
        if (f.requireActivity().isChangingConfigurations)
            return

        val removingDialog = if (f is View && f is DialogFragment) f.isRemovingRecursive else false

        if (f is View && (removingDialog || f.isPoppedRecursive || f.requireActivity().isFinishing))
            f.router?.removeView(f.viewKey)

        if (f is HostView && (f.isPoppedRecursive || f.requireActivity().isFinishing))
            f.router?.removeView(f.viewKey)
    }
}