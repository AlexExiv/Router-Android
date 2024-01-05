package com.speakerboxlite.router.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.staticCompositionLocalOf

public val LocalNavigatorTabsSaver: ProvidableCompositionLocal<NavigatorTabsSaver<*>> =
    staticCompositionLocalOf { defaultNavigatorTabsSaver() }

public fun interface NavigatorTabsSaver<Saveable : Any>
{
    public fun saver(key: String): Saver<ComposeNavigatorTabs, Saveable>
}

/**
 * Default Navigator Saver expect that on Android, your screens can be saved, by default
 * all [Screen]s are Java Serializable, this means that by default, if you only pass primitive types
 * or Java Serializable types, it will restore your screen properly.
 * If you want use Android Parcelable instead, you can, you just need to implement the Parcelable interface
 * and all types should be parcelable as well and this Default Saver should work as well.
 * Important: When using Parcelable all types should be Parcelable as well, when using Serializable all types
 * should be serializable, you can't mix both unless the types are both Parcelable and Serializable.
 *
 * If you want to use only Parcelable and want a NavigatorTabsSaver that forces the use Parcelable, you can use [parcelableNavigatorTabsSaver].
 */
public fun defaultNavigatorTabsSaver(): NavigatorTabsSaver<Any> = NavigatorTabsSaver { key ->
    mapSaver(
        save = { navigator ->
            navigator.backStackMap.toMap()
        },
        restore = { items ->
            ComposeNavigatorTabs(key, items as Map<String, List<StackEntrySaveable>>)
        }
    )
}