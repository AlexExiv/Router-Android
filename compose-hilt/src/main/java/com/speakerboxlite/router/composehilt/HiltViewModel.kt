package com.speakerboxlite.router.composehilt

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.androidhilt.AndroidHiltViewModelProvider
import com.speakerboxlite.router.compose.ViewCompose
import com.speakerboxlite.router.compose.routerViewModel

@Composable
inline fun <reified VM: com.speakerboxlite.router.ViewModel> routerHiltViewModel(
    view: ViewCompose,
    router: Router? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner" }): VM
{
    val modelProvider = AndroidHiltViewModelProvider(
        LocalContext.current,
        viewModelStoreOwner)

    return routerViewModel(
        view = view,
        router = router,
        viewModelStoreOwner = viewModelStoreOwner,
        modelProvider = modelProvider)
}
