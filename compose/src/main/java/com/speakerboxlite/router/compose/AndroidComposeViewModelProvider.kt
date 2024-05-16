package com.speakerboxlite.router.compose

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterModelProvider

class AndroidViewModelFactory<T>(val app: Application,
                                 val creator: (app: Application) -> T) : ViewModelProvider.Factory
{
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = creator(app) as T
}

class AndroidComposeViewModelProvider(val app: Application,
                                      val viewModelStoreOwner: ViewModelStoreOwner): RouterModelProvider
{
    inline fun <reified VM: ViewModel> getViewModel(noinline creator: ((app: Application) -> VM)? = null): VM
    {
        return if (creator == null)
            ViewModelProvider(viewModelStoreOwner, ViewModelProvider.AndroidViewModelFactory(app))[VM::class.java]
        else
            ViewModelProvider(viewModelStoreOwner, AndroidViewModelFactory(app, creator))[VM::class.java]
    }
}

@Composable
inline fun <reified VM: com.speakerboxlite.router.ViewModel> routerViewModel(
    view: ViewCompose,
    router: Router? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner" },
    modelProvider: RouterModelProvider = AndroidComposeViewModelProvider(LocalContext.current.applicationContext as Application, viewModelStoreOwner)): VM
{
    val _router = if (router == null)
    {
        val manager = LocalRouterManager.current ?: error("You haven't provided RouterManager")
        manager[view.viewKey] ?: error("Router for key ${view.viewKey} wasn't found")
    }
    else
        router

    val vm: VM = _router.provideViewModel(view, modelProvider)
    _router.onPrepareView(view, vm)

    DisposableEffect(view.viewKey)
    {
        vm.resultProvider.start(vm)

        onDispose {
            vm.resultProvider.pause()
        }
    }

    return vm
}
