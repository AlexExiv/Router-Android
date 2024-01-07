package com.speakerboxlite.router.fragmentcompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.compose.ComposeHostViewRoot
import com.speakerboxlite.router.fragment.HostClosableFragment
import com.speakerboxlite.router.fragment.IHostClosableFragment
import com.speakerboxlite.router.fragment._viewKey

abstract class HostComposeFragment: Fragment(),
    ComposeHostView,
    IHostClosableFragment by HostClosableFragment()
{
    override var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override lateinit var router: Router

    override var root: ComposeHostViewRoot = mutableStateOf(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        withFragment(this)
        return ComposeView(requireContext()).also {
            it.setContent {
                root.value?.invoke()
            }
        }
    }

    override fun onStart()
    {
        super.onStart()
        root.value = { ComposeNavigator() }
    }

    @Composable
    abstract fun ComposeNavigator()
}
