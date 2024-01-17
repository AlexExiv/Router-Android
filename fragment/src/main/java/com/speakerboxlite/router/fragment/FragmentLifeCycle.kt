package com.speakerboxlite.router.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.R
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.RouterManagerImpl
import com.speakerboxlite.router.RouterModelProvider
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.ViewTabs
import com.speakerboxlite.router.ViewVM
import com.speakerboxlite.router.annotations.InternalApi
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.fragment.ext.isPoppedRecursive
import com.speakerboxlite.router.fragment.ext.isRemovingRecursive
import com.speakerboxlite.router.ext.restartApp
import com.speakerboxlite.router.zombie.RouterZombie

data class FragmentModelProviderArgs(val activity: Activity,
                                     val fragment: Fragment)

typealias FragmentModelProvider = (args: FragmentModelProviderArgs) -> RouterModelProvider

@OptIn(InternalApi::class)
open class FragmentLifeCycle(private val routerManager: RouterManager,
                             private val modelProvider: FragmentModelProvider? = null): FragmentManager.FragmentLifecycleCallbacks()
{
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
            val router = routerManager.getForView(f.viewKey) ?: routerManager[f.viewKey]

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

            if (f as? ViewVM<ViewModel> != null)
            {
                val mp = modelProvider?.invoke(FragmentModelProviderArgs(f.requireActivity(), f)) ?: error("You use ViewModel without specifying FragmentModelProvider")
                f.viewModel = f.router.provideViewModel(f, mp)
            }

            f.router.onPrepareView(f, (f as? ViewVM<ViewModel>)?.viewModel)

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
            onCreateExecutor(f)?.let {
                f.router.bindExecutor(it)
            }
        }

        if (f is ViewFragment)
        {
            onCreateExecutor(f)?.let { f.localRouter.bindExecutor(it) }
            f.resultProvider.start()

            if (f is ViewTabs)
            {
                onCreateExecutor(f)?.let {
                    f.routerTabs.bindExecutor(it)
                }
            }
        }
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

    protected open fun onCreateExecutor(fragment: Fragment): CommandExecutor? =
        CommandExecutorAndroid(fragment.requireActivity(), R.id.root, fragment.childFragmentManager, fragment.requireActivity() as? HostActivityFactory, fragment as? HostCloseable)
}