package com.speakerboxlite.router

import androidx.lifecycle.ViewModelStore

interface RouterViewModelProvider<VM: ViewModel>
{
    fun getViewModel(id: String): VM
}

interface RouterViewModelStoreProvider
{
    fun getStore(id: String): ViewModelStore
    fun clear(id: String)
}
