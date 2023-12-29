package com.speakerboxlite.router.lifecycle

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.speakerboxlite.router.ComposeHostView
import com.speakerboxlite.router.command.CommandExecutorAndroid
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.R
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.RouterManagerImpl
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewFragment
import com.speakerboxlite.router.ViewTabs
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.ext.isPoppedRecursive
import com.speakerboxlite.router.ext.isRemovingRecursive
import com.speakerboxlite.router.ext.restartApp
import com.speakerboxlite.router.zombie.RouterZombie

open class FragmentLifeCycle(private val routerManager: RouterManager): FragmentManager.FragmentLifecycleCallbacks()
{
    private var resumedHoster = false

    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?)
    {
        if (f is HostView)
        {
            val router = routerManager.getForView(f.viewKey) ?: routerManager[f.viewKey]

            //In case of we couldn't find the router start the restarting process. It may occur after the app restores the state after the reboot maybe a better
            //solution is to serialize routers.
            if (router == null)
            {
                f.router = RouterZombie()

                if (!routerManager.isAppRestarting)
                {
                    (routerManager as? RouterManagerImpl)?.resetToTop()
                    f.requireActivity().restartApp()
                }
            }
            else
                f.router = router
        }

        if (f is ViewFragment)
        {
            val router = routerManager.getForView(f.viewKey)

            //In case of we couldn't find the router start the restarting process. It may occur after the app restores the state after the reboot maybe better
            //solution is to serialize routers.
            if (router == null)
            {
                f.router = RouterZombie()

                if (!routerManager.isAppRestarting)
                {
                    (routerManager as? RouterManagerImpl)?.resetToTop()
                    f.requireActivity().restartApp()
                }
            }
            else
                f.router = router

            f.localRouter = f.router.createRouterLocal(f.viewKey)
            f.router.onComposeView(f)

            if (f is ViewTabs)
                f.routerTabs = f.router.createRouterTabs(f.viewKey)
        }
    }

    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: android.view.View, savedInstanceState: Bundle?)
    {
        if (f is ViewFragment)
            f.router.onComposeAnimation(f)
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment)
    {
        super.onFragmentStarted(fm, f)

        if (f is HostView)
        {
            if (f is ComposeHostView)
            {

            }
            else
                f.router.bindExecutor(onCreateExecutor(f))
        }

        if (f is ViewFragment)
        {
            f.localRouter.bindExecutor(onCreateExecutor(f))
            f.resultProvider.start()

            if (f is ViewTabs)
                f.routerTabs.bindExecutor(onCreateExecutor(f))
        }
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment)
    {
        if (f is HostView)
        {
            if (!resumedHoster)
            {
                //f.router.topRouter = f.router
                resumedHoster = true
            }
        }

        if (f is ViewFragment)
        {
            //if (f.parentFragment == null && !resumedHoster)
                //f.router.topRouter = f.router
        }
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment)
    {
        if (f is HostView)
            resumedHoster = false
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment)
    {
        super.onFragmentStopped(fm, f)

        if (f is HostView)
        {
            f.router.unbindExecutor()
        }

        if (f is ViewFragment)
        {
            f.localRouter.unbindExecutor()
            f.resultProvider.pause()

            if (f is ViewTabs)
                f.routerTabs.unbindExecutor()
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

        if (f is ViewFragment && (removingDialog || f.isPoppedRecursive || f.requireActivity().isFinishing))
            f.router.removeView(f.viewKey)

        if (f is HostView && (f.isPoppedRecursive || f.requireActivity().isFinishing))
            f.router.removeView(f.viewKey)
    }

    protected open fun onCreateExecutor(fragment: Fragment): CommandExecutor =
        CommandExecutorAndroid(fragment.requireActivity(), R.id.root, fragment.childFragmentManager, fragment.requireActivity() as? HostActivityFactory, fragment as? HostCloseable)
}