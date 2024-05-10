package com.speakerboxlite.router.androidhilt

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import com.speakerboxlite.router.RouterModelProvider
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dagger.hilt.android.lifecycle.withCreationCallback

class AndroidViewModelFactory<T>(val app: Application,
                                 val creator: (app: Application) -> T) : ViewModelProvider.Factory
{
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = creator(app) as T
}

class AndroidHiltViewModelProvider(val context: Context,
                                   val viewModelStoreOwner: ViewModelStoreOwner): RouterModelProvider
{
    inline fun <reified VM: ViewModel> getViewModel(): VM
    {
        val extras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory)
            viewModelStoreOwner.defaultViewModelCreationExtras
        else
            CreationExtras.Empty

        val provider = ViewModelProvider(viewModelStoreOwner.viewModelStore, HiltViewModelFactory.createInternal(getActivity(), getFactory()), extras)
        return provider[VM::class.java]
    }

    inline fun <reified VM: ViewModel, reified VMF> getViewModel(noinline creator: ((VMF) -> VM)): VM
    {
        val extras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory)
            viewModelStoreOwner.defaultViewModelCreationExtras.withCreationCallback(creator)
        else
            CreationExtras.Empty.withCreationCallback(creator)

        val provider = ViewModelProvider(viewModelStoreOwner.viewModelStore, HiltViewModelFactory.createInternal(getActivity(), getFactory()), extras)
        return provider[VM::class.java]
    }

    inline fun <reified VM: ViewModel> getViewModelApp(noinline creator: ((app: Application) -> VM)): VM =
        ViewModelProvider(viewModelStoreOwner, AndroidViewModelFactory(context.applicationContext as Application, creator))[VM::class.java]

    fun getActivity(): ComponentActivity
    {
        var ctx = context
        while (ctx is ContextWrapper)
        {
            // Hilt can only be used with ComponentActivity
            if (ctx is ComponentActivity)
                return ctx

            ctx = ctx.baseContext
        }

        throw IllegalStateException("Expected an activity context for creating a HiltViewModelFactory but instead found: $ctx")
    }

    fun getFactory() : ViewModelProvider.Factory =
        if (viewModelStoreOwner is HasDefaultViewModelProviderFactory)
            viewModelStoreOwner.defaultViewModelProviderFactory
        else
            ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
}
