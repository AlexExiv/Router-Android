package com.speakerboxlite.router.samplehilt.base

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterModelProvider
import com.speakerboxlite.router.compose.AndroidViewModelFactory
import com.speakerboxlite.router.compose.ViewCompose
import com.speakerboxlite.router.compose.routerViewModel
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dagger.hilt.android.lifecycle.withCreationCallback

class AndroidHiltViewModelProvider(val context: Context,
                                   val app: Application,
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
        ViewModelProvider(viewModelStoreOwner, AndroidViewModelFactory(app, creator))[VM::class.java]

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
            ViewModelProvider.AndroidViewModelFactory(app)
}

@Composable
inline fun <reified VM: com.speakerboxlite.router.ViewModel> routerHiltViewModel(
    view: ViewCompose,
    router: Router? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner" }): VM
{
    val modelProvider = AndroidHiltViewModelProvider(
        LocalContext.current,
        LocalContext.current.applicationContext as Application,
        viewModelStoreOwner)

    return routerViewModel(
        view = view,
        router = router,
        viewModelStoreOwner = viewModelStoreOwner,
        modelProvider = modelProvider)
}
