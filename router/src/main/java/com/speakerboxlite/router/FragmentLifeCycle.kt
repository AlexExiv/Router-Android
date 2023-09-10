package com.speakerboxlite.router

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentLifeCycle(private val routerManager: RouterManager,
                        private val hostActivityFactory: HostActivityFactory): FragmentManager.FragmentLifecycleCallbacks()
{
    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?)
    {
        if (f is HostView)
        {

        }

        if (f is View<*>)
        {
            f.router = routerManager.get(f)
            f.localRouter = f.router.createRouterLocal()
            f.router.onComposeView(f)
        }
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment)
    {
        if (f is HostView)
        {
            f.router.topRouter = f.router
            f.router.bindExecutor(CommandExecutorAndroid(f.requireActivity(), R.id.root, f.childFragmentManager, hostActivityFactory))
        }

        if (f is View<*>)
        {
            f.resultProvider.start()
            f.localRouter.bindExecutor(CommandExecutorAndroid(f.requireActivity(), R.id.root, f.childFragmentManager, hostActivityFactory))
        }
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment)
    {
        if (f is HostView)
        {
            f.router.unbindExecutor()
        }

        if (f is View<*>)
        {
            f.localRouter.unbindExecutor()
            f.resultProvider.pause()
        }
    }
}