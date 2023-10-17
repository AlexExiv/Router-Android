package com.speakerboxlite.router.lifecycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.speakerboxlite.router.command.CommandExecutorAndroid
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.R
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.View

class FragmentLifeCycle(private val routerManager: RouterManager,
                        private val hostActivityFactory: HostActivityFactory): FragmentManager.FragmentLifecycleCallbacks()
{
    private var resumedHoster = false

    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?)
    {
        if (f is HostView)
        {

        }

        if (f is View)
        {
            f.router = routerManager.get(f)
            f.localRouter = f.router.createRouterLocal(f.viewKey)
            f.router.onComposeView(f)
        }
    }

    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: android.view.View, savedInstanceState: Bundle?)
    {
        if (f is View)
            f.router.onComposeAnimation(f)
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment)
    {
        if (f is HostView)
        {
            if (!resumedHoster)
            {
                f.router.topRouter = f.router
                resumedHoster = true
            }

            f.router.bindExecutor(CommandExecutorAndroid(f.requireActivity(), R.id.root, f.childFragmentManager, hostActivityFactory))
        }

        if (f is View)
        {
            if (f.parentFragment == null && !resumedHoster)
                f.router.topRouter = f.router

            f.resultProvider.start()
            f.localRouter.bindExecutor(CommandExecutorAndroid(f.requireActivity(), R.id.root, f.childFragmentManager, hostActivityFactory))
        }
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment)
    {
        if (f is HostView)
        {
            resumedHoster = false
            f.router.unbindExecutor()
        }

        if (f is View)
        {
            f.localRouter.unbindExecutor()
            f.resultProvider.pause()
        }
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment)
    {
        val activityDestroying = !(f.requireActivity().isChangingConfigurations || !f.requireActivity().isFinishing)
        if (f is View && (f.isRemoving || activityDestroying))
            f.router.removeView(f.viewKey)
    }
}