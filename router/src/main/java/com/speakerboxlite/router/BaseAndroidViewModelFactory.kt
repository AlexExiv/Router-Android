package com.speakerboxlite.router

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BaseAndroidViewModelFactory<T>(val app: Application, val creator: (app: Application) -> T) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator(app) as T
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