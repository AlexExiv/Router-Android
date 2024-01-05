package com.speakerboxlite.router.sample.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import com.speakerboxlite.router.ComposeFragmentHostView
import com.speakerboxlite.router.ComposeHostView
import com.speakerboxlite.router.ComposeHostViewRoot
import com.speakerboxlite.compose.ComposeViewHoster
import com.speakerboxlite.compose.IntentBuilder
import com.speakerboxlite.compose.ComposeNavigator
import com.speakerboxlite.compose.HostComposeFragmentFactory
import com.speakerboxlite.router.sample.base.HostFragment
import java.io.Serializable

class ComposeHostFragment: HostFragment(), ComposeHostView,
    com.speakerboxlite.compose.ComposeViewHoster,
    com.speakerboxlite.compose.HostComposeFragmentFactory
{
    override var root: ComposeHostViewRoot = mutableStateOf(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return ComposeView(requireContext()).also {
            it.setContent {
                root.value?.invoke()
            }
        }
    }

    override fun onStart()
    {
        super.onStart()
        root.value = {
            com.speakerboxlite.compose.ComposeNavigator(router = router, hoster = this, fragmentHostFactory = this)
        }
    }

    override fun start(params: Serializable?, builder: com.speakerboxlite.compose.IntentBuilder)
    {

    }

    override fun close()
    {
        parentFragmentManager.popBackStack()
    }

    override fun onCreate(): ComposeFragmentHostView = HostFragment()
}