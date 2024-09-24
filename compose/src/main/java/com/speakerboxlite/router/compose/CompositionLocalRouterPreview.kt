package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.speakerboxlite.router.RouteManagerImpl
import com.speakerboxlite.router.RouterManagerImpl
import com.speakerboxlite.router.RouterSimple
import com.speakerboxlite.router.result.ResultManagerImpl
import java.util.UUID

@Composable
fun CompositionLocalRouterPreview(content: @Composable () -> Unit)
{
    CompositionLocalProvider(
        LocalRouter provides RouterSimple(null, null, RouteManagerImpl(), RouterManagerImpl(), ResultManagerImpl()),
        LocalViewKey provides UUID.randomUUID().toString(),
        content = content)
}