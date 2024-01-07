package com.speakerboxlite.router.samplemixed.base.fragment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.compose.ComposeHostViewRoot
import com.speakerboxlite.router.fragment.HostClosableFragment
import com.speakerboxlite.router.fragment.IHostClosableFragment
import com.speakerboxlite.router.fragment._viewKey
import com.speakerboxlite.router.fragmentcompose.ComposeFragmentHostView
import com.speakerboxlite.router.fragmentcompose.ComposeHostView
import com.speakerboxlite.router.fragmentcompose.ComposeNavigatorMixed
import com.speakerboxlite.router.samplemixed.R

abstract class BaseHostFragment: Fragment(R.layout.fragment_host),
    HostView
{
    override var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override lateinit var router: Router
}

class ComposeHostFragment: BaseHostFragment(),
    ComposeFragmentHostView,
    HostCloseable
{

    override var onCloseCallback: (() -> Unit)? = null

    override fun onCloseHost()
    {
        onCloseCallback!!.invoke()
    }
}

class HostComposeFragment: com.speakerboxlite.router.fragmentcompose.HostComposeFragment()
{
    @Composable
    override fun ComposeNavigator()
    {
        ComposeNavigatorMixed(
            router = router,
            hostCloseable = this,
            fragmentHostFactory = { ComposeHostFragment() })
    }
}