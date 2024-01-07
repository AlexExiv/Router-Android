package com.speakerboxlite.router.fragmentcompose

import androidx.fragment.app.Fragment
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.fragment.FragmentLifeCycle
import com.speakerboxlite.router.fragment.FragmentModelProvider

class FragmentLifeCycleMixed(routerManager: RouterManager,
                             modelProvider: FragmentModelProvider? = null): FragmentLifeCycle(routerManager, modelProvider)
{
    override fun onCreateExecutor(fragment: Fragment): CommandExecutor? =
        if (fragment is ComposeHostView)
            null
        else
            super.onCreateExecutor(fragment)
}