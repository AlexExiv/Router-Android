package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import com.speakerboxlite.router.View
import java.io.Serializable

interface ViewCompose: View, Serializable
{
    @Composable
    fun Root()
}
