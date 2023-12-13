package com.speakerboxlite.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

interface HostView
{
    var viewKey: String
    var router: Router
}

typealias ComposeHostViewRoot = MutableState<@Composable (() -> Unit)?>

interface ComposeHostView: HostView
{
    var root: ComposeHostViewRoot
}