package com.speakerboxlite.router.fragmentcompose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.IntentBuilder
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.compose.ComposeViewHoster
import com.speakerboxlite.router.fragment._viewKey
import java.io.Serializable

abstract class ComposeHostFragment(@LayoutRes layoutId: Int): Fragment(layoutId),
    ComposeHostView,
    ComposeViewHoster,
    HostComposeFragmentFactory
{
    override var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override lateinit var router: Router

    override var root: ComposeHostViewRoot = mutableStateOf(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        ComposeView(requireContext()).also {
            it.setContent {
                root.value?.invoke()
            }
        }

    override fun onStart()
    {
        super.onStart()

        root.value = {
            ComposeNavigatorMixed(router = router, hoster = this, fragmentHostFactory = this)
        }
    }

    override fun start(params: Serializable?, builder: IntentBuilder)
    {
        error("You try to start new intent but haven't implemented this method.")
    }
}