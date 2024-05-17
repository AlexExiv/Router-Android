package com.speakerboxlite.router.fragment

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.speakerboxlite.router.RouterModelProvider

class AndroidViewModelFactory<T>(val creator: (app: Application) -> T) : ViewModelProvider.Factory
{
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = creator(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!) as T
}

class FragmentViewModelProvider(val fragment: Fragment): RouterModelProvider
{
    inline fun <reified VM: ViewModel> getViewModel(noinline creator: ((app: Application) -> VM)? = null): VM
    {
        return if (creator == null)
            ViewModelProvider(fragment)[VM::class.java]
        else
            ViewModelProvider(fragment, AndroidViewModelFactory(creator))[VM::class.java]
    }
}