package com.speakerboxlite.router.compose

import androidx.lifecycle.ViewModelStore

interface RouterViewModelStoreProvider
{
    fun getStore(id: String): ViewModelStore
    fun clear(id: String)
}

