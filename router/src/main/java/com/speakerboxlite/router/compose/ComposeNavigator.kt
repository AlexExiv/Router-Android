package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterViewModelStoreProvider
import com.speakerboxlite.router.command.CommandExecutorCompose
import com.speakerboxlite.router.command.ComposeViewHoster

typealias ComposeNavigatorContent = @Composable (navigator: ComposeNavigator) -> Unit

val LocalComposeNavigator: ProvidableCompositionLocal<ComposeNavigator?> =
    staticCompositionLocalOf { null }

val <T> ProvidableCompositionLocal<T?>.currentOrThrow: T @Composable
    get() = current ?: error("CompositionLocal is null")

val LocalRouter: ProvidableCompositionLocal<Router?> = staticCompositionLocalOf { null }

val LocalRouterViewModelStoreProvider: ProvidableCompositionLocal<RouterViewModelStoreProvider?> = staticCompositionLocalOf { null }

@Composable
fun CurrentScreen(router: Router)
{
    val navigator = LocalComposeNavigator.currentOrThrow
    val currentScreen = navigator.lastItem

    DisposableEffect(key1 = currentScreen) {
        println("Compose screen")
        currentScreen?.also {
            router.onComposeView(it.view)
        }

        onDispose {  }
    }

    currentScreen?.LocalOwnersProvider(navigator.stateHolder) { currentScreen.view.Root() }
}

@Composable 
fun ComposeNavigator(
    router: Router,
    hoster: ComposeViewHoster? = null,
    key: String = compositionUniqueId(),
    content: ComposeNavigatorContent = { CurrentScreen(router) })
{
    require(key.isNotEmpty()) { "ComposeNavigator key can't be empty" }

    val viewModelStore = LocalViewModelStoreOwner.current

    CompositionLocalProvider(LocalComposeNavigatorStateHolder providesDefault rememberSaveableStateHolder()) 
    {
        val navigator = rememberComposeNavigator(key, ComposeNavigatorViewModel.getInstance(viewModelStore!!.viewModelStore), LocalComposeNavigator.current)

        DisposableEffect(navigator) 
        {
            router.bindExecutor(CommandExecutorCompose(navigator, hoster))
            
            onDispose { router.unbindExecutor() }
        }

        CompositionLocalProvider(LocalComposeNavigator provides navigator, LocalRouter provides router) 
        {
            content(navigator)
        }
    }
}

class ComposeNavigator(val key: String,
                       internal val stateHolder: SaveableStateHolder,
                       val viewModelProvider: RouterViewModelStoreProvider?,
                       items: List<StackEntry> = listOf(),
                       val parent: ComposeNavigator? = null) : Stack<StackEntry> by SnapshotStateStack(items, viewModelProvider, 1)
{


    val lastItem: StackEntry? by derivedStateOf { lastItemOrNull }

    private val stateKeys = mutableSetOf<String>()
/*
    @Composable
    fun saveableState(key: String,
                      screen: ViewCompose? = lastItem,
                      content: @Composable () -> Unit)
    {
        val _screen = screen ?: return
        val stateKey = "${_screen.viewKey}:$key"
        stateKeys += stateKey

        @Composable
        fun provideSaveableState(suffixKey: String, content: @Composable () -> Unit)
        {
            val providedStateKey = "$stateKey:$suffixKey"
            stateKeys += providedStateKey
            stateHolder.SaveableStateProvider(providedStateKey, content)
        }

        stateHolder.SaveableStateProvider(stateKey, content)
    }
*/
    fun popUntilRoot()
    {
        popUntilRoot(this)
    }

    private tailrec fun popUntilRoot(navigator: ComposeNavigator)
    {
        navigator.popAll()

        if (navigator.parent != null)
            popUntilRoot(navigator.parent)
    }

    fun dispose(screen: ViewCompose)
    {
        //ScreenLifecycleStore.remove(screen)
        stateKeys
            .toSet() // Copy
            .asSequence()
            .filter { it.startsWith(screen.viewKey) }
            .forEach { key ->
                stateHolder.removeState(key)
                stateKeys -= key
            }
    }
}

@Composable
fun compositionUniqueId(): String = currentCompositeKeyHash.toString(MaxSupportedRadix)

private val MaxSupportedRadix = 36
