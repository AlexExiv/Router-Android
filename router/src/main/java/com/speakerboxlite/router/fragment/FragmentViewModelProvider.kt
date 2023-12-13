package com.speakerboxlite.router.fragment

import androidx.lifecycle.ViewModelStoreOwner
import com.speakerboxlite.router.RouterViewModelProvider
import com.speakerboxlite.router.ViewModel

class FragmentViewModelProvider<VM : ViewModel>(vms: ViewModelStoreOwner): RouterViewModelProvider<VM>
{
    override fun getViewModel(id: String): VM
    {
        TODO("Not yet implemented")
    }
}