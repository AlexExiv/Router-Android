package com.speakerboxlite.router.fragmentcompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView

abstract class HostComposeFragment: BaseHostComposeFragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        withFragment(this)
        return ComposeView(requireContext()).also {
            it.setContent {
                root.value?.invoke()
            }
        }
    }
}
