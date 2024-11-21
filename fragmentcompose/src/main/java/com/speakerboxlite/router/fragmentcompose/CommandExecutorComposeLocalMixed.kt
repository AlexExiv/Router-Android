package com.speakerboxlite.router.fragmentcompose

import androidx.fragment.app.Fragment
import com.speakerboxlite.router.View
import com.speakerboxlite.router.compose.CommandExecutorComposeLocal
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.ext.toStringUTF
import com.speakerboxlite.router.fragment.ViewFragment

class CommandExecutorComposeLocalMixed(navigator: ComposeNavigator): CommandExecutorComposeLocal(navigator)
{
    override fun showView(view: View)
    {
        if (view is ViewFragment)
        {
            val fragmentView = FragmentHostView(
                view.viewKey,
                (view as Fragment)::class.java,
                view.arguments?.toStringUTF())

            super.showView(fragmentView)
        }
        else
            super.showView(view)
    }
}