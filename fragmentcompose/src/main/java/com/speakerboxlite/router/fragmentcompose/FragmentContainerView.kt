package com.speakerboxlite.router.fragmentcompose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.compose.LocalComposeNavigator
import com.speakerboxlite.router.compose.ViewCompose
import com.speakerboxlite.router.compose.currentOrThrow
import java.util.UUID

interface HostComposeFragmentFactory
{
    fun onCreate(): ComposeFragmentHostView
}

val LocalHostComposeFragmentFactory: ProvidableCompositionLocal<HostComposeFragmentFactory?> = staticCompositionLocalOf { null }

class FragmentContainerView: ViewCompose
{
    override var viewKey: String = UUID.randomUUID().toString()

    @Composable
    override fun Root()
    {
        val factory = LocalHostComposeFragmentFactory.currentOrThrow
        val navigator = LocalComposeNavigator.currentOrThrow
        FragmentContainer(
            modifier = Modifier.fillMaxSize(),
            commit = {
                val view = factory.onCreate()
                view.viewKey = viewKey
                view.closeCallback = { navigator.pop() }
                replace(it, view as Fragment, viewKey)
            })
    }
}