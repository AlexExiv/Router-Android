package com.speakerboxlite.router.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.get
import com.speakerboxlite.router.RouterViewModelStoreProvider

class ComposeNavigatorViewModel: ViewModel(), RouterViewModelStoreProvider
{
    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    override fun onCleared()
    {
        for (store in viewModelStores.values)
            store.clear()

        viewModelStores.clear()
    }

    override fun getStore(id: String): ViewModelStore
    {
        var viewModelStore = viewModelStores[id]
        if (viewModelStore == null)
        {
            viewModelStore = ViewModelStore()
            viewModelStores[id] = viewModelStore
        }

        return viewModelStore
    }

    override fun clear(id: String)
    {
        val viewModelStore = viewModelStores.remove(id)
        viewModelStore?.clear()
    }

    companion object
    {
        private val FACTORY: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ComposeNavigatorViewModel() as T
        }

        @JvmStatic
        fun getInstance(viewModelStore: ViewModelStore): ComposeNavigatorViewModel =
            ViewModelProvider(viewModelStore, FACTORY).get()
    }
}