package com.speakerboxlite.router

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras

class BaseAndroidViewModelFactory<T>(val app: Application, val creator: (app: Application) -> T) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator(app) as T

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T
    {
        return creator(extras[APPLICATION_KEY]!!) as T
    }
}

class AndroidViewModelFactory<T>(val creator: (app: Application) -> T) : ViewModelProvider.Factory
{
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = creator(extras[APPLICATION_KEY]!!) as T
}

inline fun <reified T : ViewModel> Fragment.getAndroidViewModel(noinline creator: ((app: Application) -> T)? = null): T
{
    return if (creator == null)
        ViewModelProvider(this).get(T::class.java)
    else
        ViewModelProvider(this, BaseAndroidViewModelFactory(activity!!.application, creator)).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity.getAndroidViewModel(noinline creator: ((app: Application) -> T)? = null): T
{
    return if (creator == null)
        ViewModelProvider(this).get(T::class.java)
    else
        ViewModelProvider(this, BaseAndroidViewModelFactory(application, creator)).get(T::class.java)
}

inline fun <reified VM: ViewModel> ViewModelStoreOwner.getViewModel(noinline creator: ((app: Application) -> VM)? = null): VM
{
    return if (creator == null)
        ViewModelProvider(this)[VM::class.java]
    else
        ViewModelProvider(this, AndroidViewModelFactory(creator))[VM::class.java]
}