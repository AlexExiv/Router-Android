package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewModel
import java.io.Serializable

typealias ComposeHostViewRoot = MutableState<@Composable (() -> Unit)?>

interface ViewCompose: View, Serializable
{
    @Composable
    fun Root()
}

@Composable
inline fun <reified VM: ViewModel> ViewCompose.routerViewModel(
    router: Router? = null,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner" }
): VM = routerViewModel(view = this, router = router, viewModelStoreOwner = viewModelStoreOwner)
