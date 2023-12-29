package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import com.speakerboxlite.router.RouterViewModelStoreProvider


internal val LocalComposeNavigatorTabsStateHolder: ProvidableCompositionLocal<SaveableStateHolder> =
    staticCompositionLocalOf { error("LocalNavigatorStateHolder not initialized") }

@Composable
internal fun rememberComposeNavigatorTabs(key: String): ComposeNavigatorTabs
{
    val stateHolder = LocalComposeNavigatorTabsStateHolder.current
    val navigatorSaver = LocalNavigatorTabsSaver.current

    val saver = remember(navigatorSaver, stateHolder) {
        navigatorSaver.saver(key)
    }

    return rememberSaveable(saver = saver, key = key) {
        ComposeNavigatorTabs(key, mapOf())
    }
}
