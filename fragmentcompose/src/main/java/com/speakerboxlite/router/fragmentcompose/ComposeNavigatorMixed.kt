package com.speakerboxlite.router.fragmentcompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.compose.ComposeNavigatorContent
import com.speakerboxlite.router.compose.ComposeViewHoster
import com.speakerboxlite.router.compose.CurrentScreen
import com.speakerboxlite.router.compose.compositionUniqueId

@Composable
fun ComposeNavigatorMixed(key: String = compositionUniqueId(),
                          router: Router,
                          hoster: ComposeViewHoster? = null,
                          fragmentHostFactory: HostComposeFragmentFactory,
                          content: ComposeNavigatorContent = { router, navigator -> CurrentScreen(router, navigator) })
{
    CompositionLocalProvider(LocalHostComposeFragmentFactory provides fragmentHostFactory)
    {
        ComposeNavigator(key, router, hoster, content)
    }
}