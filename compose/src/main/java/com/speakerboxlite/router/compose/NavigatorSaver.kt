package com.speakerboxlite.router.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigatorSaver: ProvidableCompositionLocal<NavigatorSaver<*>> = staticCompositionLocalOf { defaultNavigatorSaver() }

fun interface NavigatorSaver<Saveable : Any>
{
    fun saver(key: String, parent: ComposeNavigator?, stateHolder: SaveableStateHolder, viewModelProvider: RouterViewModelStoreProvider?): Saver<ComposeNavigator, Saveable>
}

fun defaultNavigatorSaver(): NavigatorSaver<Any> =
    NavigatorSaver { key, parent, stateHolder, viewModelProvider ->
        listSaver(
            save = { navigator ->
                navigator.getStackEntriesSaveable()
           },
            restore = { items ->
                ComposeNavigator(key, stateHolder, viewModelProvider, items.map { StackEntry(it, viewModelProvider) })
            })
    }


fun defaultNavigatorLocalSaver(): NavigatorSaver<Any> =
    NavigatorSaver { key, parent, stateHolder, viewModelProvider ->
        listSaver(
            save = { navigator ->
                navigator.getStackEntriesSaveable()
           },
            restore = { items ->
                ComposeNavigatorLocal(key, parent, stateHolder, viewModelProvider, items.map { StackEntry(it, viewModelProvider) })
            })
    }

