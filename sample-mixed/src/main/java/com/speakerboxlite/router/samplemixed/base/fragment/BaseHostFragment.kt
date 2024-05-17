package com.speakerboxlite.router.samplemixed.base.fragment

import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.IntentBuilder
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.fragment.AnimationControllerFragment
import com.speakerboxlite.router.fragment.bootstrap.HostFragment
import com.speakerboxlite.router.fragment.ext._viewKey
import com.speakerboxlite.router.fragmentcompose.CommandExecutorComposeMixed
import com.speakerboxlite.router.fragmentcompose.ComposeFragmentHostView
import com.speakerboxlite.router.fragmentcompose.ComposeViewHoster
import com.speakerboxlite.router.samplemixed.R
import com.speakerboxlite.router.samplemixed.base.animations.AnimationControllerFragmentDefault
import java.io.Serializable

open class BaseHostFragment: HostFragment(R.layout.fragment_host)

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

class ComposeViewHosterImpl(val context: Context): ComposeViewHoster
{
    override fun onCreateComposeFragmentHostView(): ComposeFragmentHostView =
        ComposeHostFragment()

    override fun onCreateAnimation(): AnimationControllerFragment<RoutePath, View>? =
        AnimationControllerFragmentDefault()

    override fun start(params: Serializable?, builder: IntentBuilder)
    {
        TODO("Not yet implemented")
    }
}

class HostComposeFragment: com.speakerboxlite.router.fragmentcompose.HostComposeFragment()
{
    @Composable
    override fun Navigator()
    {
        ComposeNavigator(
            router = router,
            hostCloseable = this,
            executorFactory = { CommandExecutorComposeMixed(it, R.id.root, parentFragmentManager, ComposeViewHosterImpl(requireContext()), this) })
    }
}

class TabHostComposeFragment: Fragment(R.layout.fragment_host)
{
    var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        if (childFragmentManager.backStackEntryCount == 0)
        {
            val host = HostComposeFragment()
            host.viewKey = viewKey
            childFragmentManager.beginTransaction()
                .replace(R.id.root, host, viewKey)
                .addToBackStack(viewKey)
                .commit()
        }
    }
}