package com.speakerboxlite.router.fragmentcompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.speakerboxlite.router.HostView

typealias ComposeHostViewRoot = MutableState<@Composable (() -> Unit)?>

/**
 * Host fragment that contains compose views and provides smooth transition from a fragment view to a compose view.
 */
interface ComposeHostView: HostView
{
    var root: ComposeHostViewRoot
}

/**
 * Host fragment that contains other fragments and provides smooth transition from a compose view to a fragment view
 */
interface ComposeFragmentHostView: HostView
{
    var closeCallback: (() -> Unit)?
}