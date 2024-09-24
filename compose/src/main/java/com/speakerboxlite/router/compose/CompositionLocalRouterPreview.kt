package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.speakerboxlite.router.annotations.InternalApi
import com.speakerboxlite.router.zombie.RouterZombie
import java.util.UUID

@Composable
@OptIn(InternalApi::class)
fun CompositionLocalRouterPreview(content: @Composable () -> Unit)
{
    CompositionLocalProvider(
        LocalRouter provides RouterZombie(),
        LocalViewKey provides UUID.randomUUID().toString(),
        content = content)
}