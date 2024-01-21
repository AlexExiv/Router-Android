package com.speakerboxlite.router.fragmentcompose

import androidx.fragment.app.Fragment
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.R
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.fragment.FragmentLifeCycle
import com.speakerboxlite.router.fragment.FragmentModelProvider

class FragmentLifeCycleMixed(routerManager: RouterManager,
                             modelProvider: FragmentModelProvider? = null,
                             val fragmentFactory: HostFragmentComposeFactory? = null): FragmentLifeCycle(routerManager, modelProvider)
{
    override fun onCreateExecutor(fragment: Fragment): CommandExecutor? =
        if (fragment is ComposeHostView)
            null
        else
            CommandExecutorFragmentMixed(fragment.requireActivity(), R.id.root, fragment.childFragmentManager, fragment.requireActivity() as? HostActivityFactory, fragment as? HostCloseable, fragmentFactory)
}