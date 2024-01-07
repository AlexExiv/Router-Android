package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

class StackEntry(val view: ViewCompose,
                 val viewModelProvider: RouterViewModelStoreProvider?,
                 val animationController: AnimationControllerCompose?,
                 isPopped: Boolean = false): ViewModelStoreOwner
{
    constructor(entry: StackEntrySaveable, viewModelProvider: RouterViewModelStoreProvider?):
            this(entry.view, viewModelProvider, entry.animationController, entry.isPopped)

    val id: String get() = view.viewKey

    override val viewModelStore: ViewModelStore get() = viewModelProvider?.getStore(id) ?: error("")

    var isRemoving: Boolean = isPopped
        private set

    @Composable
    fun LocalOwnersProvider(saveableStateHolder: SaveableStateHolder, content: @Composable () -> Unit)
    {
        CompositionLocalProvider(LocalViewModelStoreOwner provides this)
        {
            saveableStateHolder.SaveableStateProvider(id, content)
        }
    }

    fun onDispose()
    {
        viewModelProvider?.clear(id)
    }

    internal fun makeRemoving()
    {
        isRemoving = true
    }
}