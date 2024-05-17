package com.speakerboxlite.router.composehilt

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.compose.ViewCompose

@Composable
inline fun <reified VM: ViewModel> ViewCompose.routerHiltViewModel(
    router: Router? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner" }
): VM = routerHiltViewModel(view = this, router = router, viewModelStoreOwner = viewModelStoreOwner)
