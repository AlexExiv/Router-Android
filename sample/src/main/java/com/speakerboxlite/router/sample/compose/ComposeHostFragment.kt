package com.speakerboxlite.router.sample.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import com.speakerboxlite.router.ComposeHostView
import com.speakerboxlite.router.ComposeHostViewRoot
import com.speakerboxlite.router.command.ComposeViewHoster
import com.speakerboxlite.router.command.IntentBuilder
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.sample.base.HostFragment
import java.io.Serializable

class ComposeHostFragment: HostFragment(), ComposeHostView, ComposeViewHoster
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
            ComposeNavigator(router = router, hoster = this)
        }
    }

    override fun start(params: Serializable?, builder: IntentBuilder)
    {

    }

    override fun close()
    {
        parentFragmentManager.popBackStack()
    }
}