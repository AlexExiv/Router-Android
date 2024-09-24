package com.speakerboxlite.router.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.cache
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.Result
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterConfigGlobal
import com.speakerboxlite.router.RouterResultDispatcher
import com.speakerboxlite.router.ViewBTS
import com.speakerboxlite.router.ViewDialog
import com.speakerboxlite.router.ViewResult
import com.speakerboxlite.router.command.CommandExecutor
import java.lang.ref.WeakReference

@Composable
fun SingleContent(router: Router, navigator: ComposeNavigator)
{
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight())
    {
        val stackEntry = navigator.lastFullItem

        CompleteTransitionEffect(stackEntry = stackEntry, navigator = navigator)
        stackEntry?.LocalOwnersProvider(navigator.stateHolder)
        {
            stackEntry.view.Root()
        }
    }
}

@Composable
fun ComposeNavigatorLocal(
    key: String = LocalViewKey.currentOrThrow,
    router: Router = LocalRouter.currentOrThrow,
    path: RoutePath?,
    executorFactory: CommandExecutorFactory = CommandExecutorFactory { CommandExecutorComposeLocal(it) },
    content: ComposeNavigatorContent = { router, navigator -> SingleContent(router, navigator) })
{
    val localRouter = remember(key) { router.createRouterLocal(key) }

    path ?: return
    var changed = false
    val _path = rememberSaveable(inputs = arrayOf(path)) {
        changed = true
        path
    }

    ComposeNavigatorLocal(
        key = key,
        router = localRouter,
        executorFactory = executorFactory,
        content = content)

    DisposableEffect(key1 = _path) {

        if (changed)
            localRouter.routeInContainer(0, _path)

        onDispose {  }
    }
}

@Composable
inline fun <VR: ViewResult, R: Any> rememberResultCallback(crossinline calculation: @DisallowComposableCalls (result: Result<VR, R>) -> Unit): RouterResultDispatcher<VR, R> =
    currentComposer.cache(false) { RouterResultDispatcher { calculation(it) } }

@Composable
fun <VR: ViewResult, R: Any> ComposeNavigatorLocal(
    key: String = LocalViewKey.currentOrThrow,
    router: Router = LocalRouter.currentOrThrow,
    viewResult: VR,
    path: RoutePathResult<R>?,
    result: RouterResultDispatcher<VR, R>,
    executorFactory: CommandExecutorFactory = CommandExecutorFactory { CommandExecutorComposeLocal(it) },
    content: ComposeNavigatorContent = { router, navigator -> SingleContent(router, navigator) })
{
    val localRouter = remember(key) { router.createRouterLocal(key) }

    path ?: return
    var changed = false
    val _path = rememberSaveable(inputs = arrayOf(path)) {
        changed = true
        path
    }

    ComposeNavigatorLocal(
        key = key,
        router = localRouter,
        executorFactory = executorFactory,
        content = content)

    DisposableEffect(key1 = _path)
    {
        if (changed)
            localRouter.routeInContainerWithResult(viewResult, 0, _path, result)

        onDispose { }
    }
}

@Composable
fun ComposeNavigatorLocal(
    key: String,
    router: Router,
    executorFactory: CommandExecutorFactory = CommandExecutorFactory { CommandExecutorComposeLocal(it) },
    content: ComposeNavigatorContent = { router, navigator -> SingleContent(router, navigator) })
{
    require(key.isNotEmpty()) { "ComposeNavigator key can't be empty" }

    val viewModelStore = LocalViewModelStoreOwner.current

    CompositionLocalProvider(
        LocalComposeNavigatorStateHolder providesDefault rememberSaveableStateHolder(),
        LocalNavigatorSaver providesDefault defaultNavigatorLocalSaver())
    {
        val navigator = rememberComposeNavigatorLocal(key,
            listOf(),
            ComposeNavigatorViewModel.getInstance(viewModelStore!!.viewModelStore))

        DisposableEffect(navigator)
        {
            navigator.onDisposeCallback = OnDisposeCallback { key -> router.removeView(key) }
            router.bindExecutor(executorFactory.onCreate(navigator))

            onDispose {
                navigator.tryDispose()
                router.unbindExecutor()
            }
        }

        CompositionLocalProvider(LocalRouter provides router)
        {
            content(router, navigator)
        }
    }
}

class ComposeNavigatorLocal(
    key: String,
    parent: ComposeNavigator?,
    stateHolder: SaveableStateHolder,
    viewModelProvider: RouterViewModelStoreProvider?,
    items: List<StackEntry> = listOf()): ComposeNavigator(key, stateHolder, viewModelProvider, items)
{
    val weakParent = WeakReference(parent)
    val parent: ComposeNavigator? get() = weakParent.get()

    override fun push(view: ViewCompose, animationController: AnimationControllerCompose?)
    {
        super.push(view, animationController)
        addSubViewKey("", view.viewKey)
    }

    override fun addSubViewKey(parentKey: String, viewKey: String)
    {
        parent?.addSubViewKey(key, viewKey)
    }

    override fun removeSubViewKey(parentKey: String, viewKey: String)
    {
        parent?.removeSubViewKey(key, viewKey)
    }

    override fun removeLast(n: Int): List<StackEntry>
    {
        val subs = super.removeLast(n)
        subs.forEach { removeSubViewKey("", it.id) }
        return subs
    }

    companion object
    {
        val TAG = "ComposeNavigatorLocal"
    }
}