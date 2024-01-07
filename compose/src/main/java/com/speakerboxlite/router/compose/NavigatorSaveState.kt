package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalComposeNavigatorStateHolder: ProvidableCompositionLocal<SaveableStateHolder> =
    staticCompositionLocalOf { error("LocalNavigatorStateHolder not initialized") }

@Composable
internal fun rememberComposeNavigator(key: String,
                                      initialBackStack: List<StackEntrySaveable> = listOf(),
                                      viewModelProvider: RouterViewModelStoreProvider?): ComposeNavigator
{
    val stateHolder = LocalComposeNavigatorStateHolder.current
    val navigatorSaver = LocalNavigatorSaver.current

    val saver = remember(key, navigatorSaver, stateHolder, viewModelProvider) {
        navigatorSaver.saver(key, stateHolder, viewModelProvider)
    }

    return rememberSaveable(inputs = arrayOf(key), saver = saver, key = key) {
        ComposeNavigator(key, stateHolder, viewModelProvider, initialBackStack.map { StackEntry(it, viewModelProvider) })
    }
}
