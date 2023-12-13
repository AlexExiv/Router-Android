package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.RouterViewModelStoreProvider

class StackEntry(val view: ViewCompose,
                 val viewModelProvider: RouterViewModelStoreProvider?): ViewModelStoreOwner
{
    constructor(entry: StackEntrySaveable, viewModelProvider: RouterViewModelStoreProvider?):
            this(entry.view, viewModelProvider)

    val id: String get() = view.viewKey

    override val viewModelStore: ViewModelStore get() = viewModelProvider?.getStore(id) ?: error("")

    @Composable
    fun LocalOwnersProvider(saveableStateHolder: SaveableStateHolder, content: @Composable () -> Unit)
    {
        CompositionLocalProvider(LocalViewModelStoreOwner provides this)
        {
            saveableStateHolder.SaveableStateProvider(id, content)
        }
    }
}