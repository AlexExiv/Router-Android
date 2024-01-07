package com.speakerboxlite.router.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.staticCompositionLocalOf
import java.io.Serializable

val LocalNavigatorSaver: ProvidableCompositionLocal<NavigatorSaver<*>> = staticCompositionLocalOf { defaultNavigatorSaver() }

fun interface NavigatorSaver<Saveable : Any>
{
    fun saver(key: String, stateHolder: SaveableStateHolder, viewModelProvider: RouterViewModelStoreProvider?): Saver<ComposeNavigator, Saveable>
}

fun defaultNavigatorSaver(): NavigatorSaver<Any> =
    NavigatorSaver { key, stateHolder, viewModelProvider ->
        listSaver(
            save = { navigator ->
                navigator.getStackEntriesSaveable()
           },
            restore = { items ->
                ComposeNavigator(key, stateHolder, viewModelProvider, items.map { StackEntry(it, viewModelProvider) })
            })
    }

data class StackEntrySaveable(val view: ViewCompose,
                              val animationController: AnimationControllerCompose?,
                              val isPopped: Boolean): Serializable
