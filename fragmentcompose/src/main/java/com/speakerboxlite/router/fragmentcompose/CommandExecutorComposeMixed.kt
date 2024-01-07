package com.speakerboxlite.router.fragmentcompose

import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.compose.AnimationControllerCompose
import com.speakerboxlite.router.compose.CommandExecutorCompose
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.compose.ComposeViewHoster

class CommandExecutorComposeMixed(navigator: ComposeNavigator,
                                  hoster: ComposeViewHoster? = null,
                                  hostCloseable: HostCloseable? = null): CommandExecutorCompose(navigator, hoster, hostCloseable)
{
    override fun changeHost(key: String, animationController: AnimationControllerCompose?)
    {
        val host = FragmentContainerView()
        host.viewKey = key
        navigator.push(host, animationController)
    }
}