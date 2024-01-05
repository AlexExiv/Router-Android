package com.speakerboxlite.router.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.staticCompositionLocalOf
import com.speakerboxlite.router.RouterViewModelStoreProvider
import java.io.Serializable


public val LocalNavigatorSaver: ProvidableCompositionLocal<NavigatorSaver<*>> =
    staticCompositionLocalOf { defaultNavigatorSaver() }

public fun interface NavigatorSaver<Saveable : Any> {
    public fun saver(
        key: String,
        stateHolder: SaveableStateHolder,
        viewModelProvider: RouterViewModelStoreProvider?,
    ): Saver<ComposeNavigator, Saveable>
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
 * If you want to use only Parcelable and want a NavigatorSaver that forces the use Parcelable, you can use [parcelableNavigatorSaver].
 */
public fun defaultNavigatorSaver(): NavigatorSaver<Any> = NavigatorSaver { key, stateHolder, viewModelProvider ->
    listSaver(
        save = { navigator ->
            navigator.getStackEntriesSaveable()
       },
        restore = { items ->
            ComposeNavigator(key, stateHolder, viewModelProvider, items.map { StackEntry(it, viewModelProvider) })
        }
    )
}

data class StackEntrySaveable(val view: ViewCompose,
                              val animationController: AnimationControllerCompose?,
                              val isPopped: Boolean): Serializable
