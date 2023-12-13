package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import com.speakerboxlite.router.RouterViewModelStoreProvider

internal val LocalComposeNavigatorStateHolder: ProvidableCompositionLocal<SaveableStateHolder> =
    staticCompositionLocalOf { error("LocalNavigatorStateHolder not initialized") }

@Composable
internal fun rememberComposeNavigator(key: String,
                                      viewModelProvider: RouterViewModelStoreProvider?,
                                      parent: ComposeNavigator?): ComposeNavigator
{
    val stateHolder = LocalComposeNavigatorStateHolder.current
    val navigatorSaver = LocalNavigatorSaver.current

    val saver = remember(navigatorSaver, stateHolder, parent, viewModelProvider) {
        navigatorSaver.saver(key, stateHolder, viewModelProvider, parent)
    }

    return rememberSaveable(saver = saver, key = key) {
        ComposeNavigator(key, stateHolder, viewModelProvider, listOf(), parent)
    }
}
