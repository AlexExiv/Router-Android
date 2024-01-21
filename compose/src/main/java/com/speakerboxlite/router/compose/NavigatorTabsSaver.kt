package com.speakerboxlite.router.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigatorTabsSaver: ProvidableCompositionLocal<NavigatorTabsSaver<*>> = staticCompositionLocalOf { defaultNavigatorTabsSaver() }

fun interface NavigatorTabsSaver<Saveable : Any>
{
    fun saver(key: String): Saver<ComposeNavigatorTabs, Saveable>
}

fun defaultNavigatorTabsSaver(): NavigatorTabsSaver<Any> =
    NavigatorTabsSaver { key ->
        mapSaver(
            save = { navigator ->
                navigator.backStackMap.toMap()
            },
            restore = { items ->
                ComposeNavigatorTabs(key, items as Map<String, List<StackEntrySaveable>>)
        })
    }