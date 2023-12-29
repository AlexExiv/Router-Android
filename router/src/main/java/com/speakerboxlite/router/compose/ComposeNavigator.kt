package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.RouterViewModelStoreProvider
import com.speakerboxlite.router.command.CommandExecutorCompose
import com.speakerboxlite.router.command.ComposeViewHoster

typealias ComposeNavigatorContent = @Composable (router: Router, navigator: ComposeNavigator) -> Unit

val LocalComposeNavigator: ProvidableCompositionLocal<ComposeNavigator?> =
    staticCompositionLocalOf { null }

val <T> ProvidableCompositionLocal<T?>.currentOrThrow: T @Composable
    get() = current ?: error("CompositionLocal is null")

val LocalRouter: ProvidableCompositionLocal<Router?> = staticCompositionLocalOf { null }

val LocalRouterTabs: ProvidableCompositionLocal<RouterTabs?> = staticCompositionLocalOf { null }

val LocalRouterViewModelStoreProvider: ProvidableCompositionLocal<RouterViewModelStoreProvider?> = staticCompositionLocalOf { null }

@Composable
fun CurrentScreen(router: Router, navigator: ComposeNavigator)
{
    val stackItem = navigator.lastItem

    DisposableEffect(key1 = stackItem) {

        stackItem?.also {
            if (it.view !is FragmentContainerView)
                router.onComposeView(it.view)
        }

        onDispose {  }
    }

    stackItem?.LocalOwnersProvider(navigator.stateHolder) { stackItem.view.Root() }
}

@Composable 
fun ComposeNavigator(
    router: Router,
    hoster: ComposeViewHoster? = null,
    key: String = compositionUniqueId(),
    fragmentHostFactory: HostComposeFragmentFactory? = null,
    content: ComposeNavigatorContent = { router, navigator -> CurrentScreen(router, navigator) })
{
    require(key.isNotEmpty()) { "ComposeNavigator key can't be empty" }

    val viewModelStore = LocalViewModelStoreOwner.current

    CompositionLocalProvider(LocalComposeNavigatorStateHolder providesDefault rememberSaveableStateHolder(),
        LocalHostComposeFragmentFactory provides fragmentHostFactory)
    {
        val navigator = rememberComposeNavigator(key,
            listOf(),
            ComposeNavigatorViewModel.getInstance(viewModelStore!!.viewModelStore))

        DisposableEffect(navigator) 
        {
            router.bindExecutor(CommandExecutorCompose(navigator, hoster, null))
            
            onDispose {
                router.unbindExecutor()
            }
        }

        CompositionLocalProvider(LocalComposeNavigator provides navigator,
            LocalRouter provides router)
        {
            content(router, navigator)
        }
    }
}

class ComposeNavigator(val key: String,
                       val stateHolder: SaveableStateHolder,
                       val viewModelProvider: RouterViewModelStoreProvider?,
                       items: List<StackEntry> = listOf(),
                       val minSize: Int = 1)
{
    internal val stateStack: SnapshotStateList<StackEntry> = mutableStateListOf()

    init
    {
        stateStack.addAll(items)
    }

    val items: List<StackEntry> by derivedStateOf { stateStack.toList() }

    val lastItem: StackEntry? by derivedStateOf { stateStack.lastOrNull() }

    val lastItemOrNull: StackEntry? by derivedStateOf { stateStack.lastOrNull() }

    val size: Int by derivedStateOf { stateStack.size }

    val isEmpty: Boolean by derivedStateOf { stateStack.isEmpty() }

    val canPop: Boolean by derivedStateOf { stateStack.size > minSize }

    fun push(view: ViewCompose)
    {
        stateStack += StackEntry(view, viewModelProvider)
    }

    fun replace(view: ViewCompose)
    {
        if (stateStack.isEmpty())
            push(view)
        else
            stateStack[stateStack.lastIndex] = StackEntry(view, viewModelProvider)
    }

    fun pop()
    {
        if (canPop)
            removeLast()
    }

    fun popAll()
    {
        popUntil { false }
    }

    fun popToRoot()
    {
        while (stateStack.size > minSize)
            removeLast()
    }

    fun popUntil(predicate: (StackEntry) -> Boolean)
    {
        while (canPop && stateStack.isNotEmpty())
        {
            if (predicate(stateStack.last()))
                break

            removeLast()
        }
    }

    fun completeTransition(entry: StackEntry?)
    {
        if (entry?.isRemoving == true)
            entry.onDispose()
    }

    protected fun removeLast()
    {
        val se = stateStack.removeLast()
        se.makeRemoving()
    }

    fun getStackEntriesSaveable() = items.map { StackEntrySaveable(it.view, it.isRemoving) }
}

@Composable
fun compositionUniqueId(): String = currentCompositeKeyHash.toString(MaxSupportedRadix)

private val MaxSupportedRadix = 36
