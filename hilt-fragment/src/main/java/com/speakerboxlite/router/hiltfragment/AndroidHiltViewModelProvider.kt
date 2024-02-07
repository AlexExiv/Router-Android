package com.speakerboxlite.router.hiltfragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.speakerboxlite.router.RouterModelProvider
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories
import dagger.hilt.android.lifecycle.withCreationCallback

class AndroidHiltViewModelProvider(val fragment: Fragment): RouterModelProvider
{
    inline fun <reified VM: ViewModel> getViewModel(): VM =
        ViewModelProvider(fragment, DefaultViewModelFactories.getFragmentFactory(fragment, fragment.defaultViewModelProviderFactory))[VM::class.java]

    inline fun <reified VM: ViewModel, reified VMF> getViewModel(noinline creator: ((vmf: VMF) -> VM)): VM
    {
        ViewModelProvider(
            fragment.viewModelStore,
            DefaultViewModelFactories.getFragmentFactory(fragment, fragment.defaultViewModelProviderFactory),
            fragment.defaultViewModelCreationExtras.withCreationCallback<VMF> { factory -> creator(factory) })[VM::class.java]
    }
}