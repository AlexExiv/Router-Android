package com.speakerboxlite.router.fragmentcompose

import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.compose.ComposeHostViewRoot

/**
 * Host fragment that contains compose views and provides smooth transition from a fragment view to a compose view.
 */
interface ComposeHostView: HostView
{
    var root: ComposeHostViewRoot // unused
}

/**
 * Host fragment that contains other fragments and provides smooth transition from a compose view to a fragment view
 */
interface ComposeFragmentHostView: HostView
{
    var onCloseCallback: (() -> Unit)? // unused
}