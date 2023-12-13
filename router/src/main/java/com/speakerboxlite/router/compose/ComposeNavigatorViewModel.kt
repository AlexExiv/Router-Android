package com.speakerboxlite.router.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.get
import com.speakerboxlite.router.RouterViewModelStoreProvider

class ComposeNavigatorViewModel: ViewModel(), RouterViewModelStoreProvider
{
    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    fun clear(backStackEntryId: String) {
        // Clear and remove the NavGraph's ViewModelStore
        val viewModelStore = viewModelStores.remove(backStackEntryId)
        viewModelStore?.clear()
    }

    override fun onCleared() {
        for (store in viewModelStores.values) {
            store.clear()
        }
        viewModelStores.clear()
    }

    override fun getStore(id: String): ViewModelStore {
        var viewModelStore = viewModelStores[id]
        if (viewModelStore == null) {
            viewModelStore = ViewModelStore()
            viewModelStores[id] = viewModelStore
        }
        return viewModelStore
    }

    companion object
    {
        private val FACTORY: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ComposeNavigatorViewModel() as T
            }
        }

        @JvmStatic
        fun getInstance(viewModelStore: ViewModelStore): ComposeNavigatorViewModel {
            val viewModelProvider = ViewModelProvider(viewModelStore, FACTORY)
            return viewModelProvider.get()
        }
    }
}