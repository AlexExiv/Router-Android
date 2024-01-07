package com.speakerboxlite.router.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.ViewBTS
import com.speakerboxlite.router.ViewDialog
import kotlinx.coroutines.launch

typealias ComposeNavigatorContent = @Composable (router: Router, navigator: ComposeNavigator) -> Unit

val LocalComposeNavigator: ProvidableCompositionLocal<ComposeNavigator?> =
    staticCompositionLocalOf { null }

val <T> ProvidableCompositionLocal<T?>.currentOrThrow: T @Composable
    get() = current ?: error("CompositionLocal is null")

val LocalRouter: ProvidableCompositionLocal<Router?> = staticCompositionLocalOf { null }

val LocalRouterTabs: ProvidableCompositionLocal<RouterTabs?> = staticCompositionLocalOf { null }

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CurrentScreen(router: Router, navigator: ComposeNavigator)
{
    val stackEntry = navigator.lastFullItem ?: return
    val stackDialog = navigator.lastDialog
    val stackBottomSheet = navigator.lastBottomSheet

    val coroutineScope = rememberCoroutineScope()

    ComposeViewEffect(stackEntry = stackEntry, router = router)
    ComposeViewEffect(stackEntry = stackDialog, router = router)
    ComposeViewEffect(stackEntry = stackBottomSheet, router = router)

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            if (it == ModalBottomSheetValue.Hidden && stackBottomSheet == navigator.lastItem)
                router.back()
            true
        })

    ModalBottomSheetLayout(
        sheetContent = {
            stackBottomSheet?.LocalOwnersProvider(navigator.stateHolder)
            {
                stackBottomSheet.view.Root()
            }
        },
        sheetState = sheetState)
    {
        navigator.beginTransition()

        AnimatedContent(targetState = stackEntry,
            transitionSpec = {
                stackEntry.animationController?.prepareAnimation(navigator, this) ?: ContentTransformNone
            },
            label = "Backstack",
            contentKey = { it.id })
        { se ->

            CompleteTransitionEffect(stackEntry = se, navigator = navigator)
            se.LocalOwnersProvider(navigator.stateHolder)
            {
                se.view.Root()
            }
        }
    }

    if (stackBottomSheet != null)
    {
        DisposableEffect(key1 = stackBottomSheet.view.viewKey)
        {
            coroutineScope.launch { sheetState.show() }

            onDispose {
                coroutineScope.launch { sheetState.hide() }
            }
        }
    }

    if (stackDialog != null)
    {
        Dialog(onDismissRequest = { router.back() })
        {
            stackDialog.LocalOwnersProvider(saveableStateHolder = navigator.stateHolder)
            {
                stackDialog.view.Root()
            }
        }
    }
}

@Composable 
fun ComposeNavigator(
    key: String = compositionUniqueId(),
    router: Router,
    hoster: ComposeViewHoster? = null,
    content: ComposeNavigatorContent = { router, navigator -> CurrentScreen(router, navigator) })
{
    require(key.isNotEmpty()) { "ComposeNavigator key can't be empty" }

    val viewModelStore = LocalViewModelStoreOwner.current

    CompositionLocalProvider(LocalComposeNavigatorStateHolder providesDefault rememberSaveableStateHolder())
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

    var poppingEntries: List<StackEntry>? = null
        private set

    var isAnimating: Boolean = false
        private set

    init
    {
        stateStack.addAll(items)
    }

    val items: List<StackEntry> by derivedStateOf { stateStack.toList() }

    val lastItem: StackEntry? by derivedStateOf { stateStack.lastOrNull() }
    val lastFullItem: StackEntry? by derivedStateOf { stateStack.lastOrNull { it.view !is ViewDialog && it.view !is ViewBTS } }
    val lastDialog: StackEntry? by derivedStateOf {
        if (lastItem?.view is ViewDialog)
            lastItem
        else
            null
    }
    val lastBottomSheet: StackEntry? by derivedStateOf {
        if (lastItem?.view is ViewBTS)
            lastItem
        else
            null
    }

    val size: Int by derivedStateOf { stateStack.size }

    val isEmpty: Boolean by derivedStateOf { stateStack.isEmpty() }

    val canPop: Boolean by derivedStateOf { stateStack.size > minSize }

    fun push(view: ViewCompose, animationController: AnimationControllerCompose?)
    {
        stateStack += StackEntry(view, viewModelProvider, animationController)
    }

    fun replace(view: ViewCompose)
    {
        if (stateStack.isEmpty())
            push(view, null)
        else
            stateStack[stateStack.lastIndex] = StackEntry(view, viewModelProvider, null)
    }

    fun pop()
    {
        if (canPop)
            removeLast()
    }

    fun popToRoot()
    {
        while (stateStack.size > minSize)
            removeLast(stateStack.size - 1)
    }

    fun popUntil(predicate: (StackEntry) -> Boolean)
    {
        var n = 0
        while (canPop && stateStack.isNotEmpty())
        {
            if (predicate(stateStack[stateStack.size - n - 1]))
                break

            n += 1
        }

        if (n > 0)
            removeLast(n)
    }

    fun beginTransition()
    {
        isAnimating = true
    }

    fun completeTransition()
    {
        isAnimating = false
        onDisposePopping()
    }

    protected fun removeLast(n: Int = 1)
    {
        onDisposePopping()

        poppingEntries = stateStack.subList(stateStack.size - n, stateStack.size).toList()
        poppingEntries?.forEach { it.makeRemoving() }
        stateStack.removeRange(stateStack.size - n, stateStack.size)
    }

    protected fun onDisposePopping()
    {
        poppingEntries?.forEach { it.onDispose() }
        poppingEntries = null
    }

    fun getStackEntriesSaveable() = items.map { StackEntrySaveable(it.view, it.animationController, it.isRemoving) }
}

@Composable
fun compositionUniqueId(): String = currentCompositeKeyHash.toString(MaxSupportedRadix)

private val MaxSupportedRadix = 36
