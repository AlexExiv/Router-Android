package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.speakerboxlite.router.View
import java.io.Serializable

typealias ComposeHostViewRoot = MutableState<@Composable (() -> Unit)?>

interface ViewCompose: View, Serializable
{
    @Composable
    fun Root()
}
