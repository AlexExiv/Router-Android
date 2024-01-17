package com.speakerboxlite.router.fragmentcompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.compose.ComposeHostViewRoot
import com.speakerboxlite.router.fragment.HostClosableFragment
import com.speakerboxlite.router.fragment.IHostClosableFragment
import com.speakerboxlite.router.fragment._viewKey

abstract class BaseHostComposeFragment: Fragment(),
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

    override fun onStart()
    {
        super.onStart()
        root.value = { Navigator() }
    }

    @Composable
    abstract fun Navigator()
}
