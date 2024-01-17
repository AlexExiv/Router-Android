package com.speakerboxlite.router.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterTabs

typealias ComposeNavigatorTabsContent =
        @Composable (router: Router, navigator: ComposeNavigator, lastTab: Int, selectedTab: Int) -> Unit

@Composable
fun CurrentScreenTab(router: Router,
                     navigator: ComposeNavigator,
                     lastTab: Int,
                     selectedTab: Int,
                     transitionTabsSpec: AnimatedContentTransitionScope<StackEntry>.(prevTab: Int, currentTab: Int) -> ContentTransform)
{
    val stackEntry = navigator.lastFullItem ?: return

    var recomposeStep by remember(selectedTab) { mutableIntStateOf(0) }

    SideEffect { recomposeStep += 1 }

    navigator.beginTransition()

    AnimatedContent(
        targetState = stackEntry,
        transitionSpec = {
            if (recomposeStep == 0 && navigator.stateStack.size < 2)
                transitionTabsSpec(this, lastTab, selectedTab)
            else
                stackEntry.animationController?.prepareAnimation(navigator, this) ?: ContentTransformNone
        },
        label = "Tabs-Backstack",
        contentKey = { it.id })
    {
        se ->

        CompleteTransitionEffect(stackEntry = se, navigator = navigator)
        se.LocalOwnersProvider(navigator.stateHolder) { se.view.Root() }
    }
}

@Composable
fun ComposeNavigatorTabs(key: String = compositionUniqueId(),
                         routerTabs: RouterTabs,
                         tabPaths: List<RoutePath>,
                         selectedTab: Int,
                         hoster: ComposeViewHoster? = null,
                         hostCloseable: HostCloseable? = null,
                         transitionTabsSpec: AnimatedContentTransitionScope<StackEntry>.(prevTab: Int, currentTab: Int) -> ContentTransform = { p, c -> contentTransformChangeTab(p, c) },
                         onTabChanged: (Int) -> Unit,
                         executorFactory: CommandExecutorFactory = CommandExecutorFactory { CommandExecutorCompose(it, hoster, hostCloseable) },
                         content: ComposeNavigatorTabsContent = { router, navigator, lastTab, selectedTab -> CurrentScreenTab(router, navigator, lastTab, selectedTab, transitionTabsSpec) })
{
    val viewModelStore = LocalViewModelStoreOwner.current
    val tabs = remember(tabPaths) {
        tabPaths.mapIndexed { i, p -> routerTabs.route(i, p, false) }
    }
    val router = routerTabs[selectedTab]

    DisposableEffect(key1 = routerTabs)
    {
        routerTabs.tabChangeCallback = { onTabChanged(it) }

        onDispose {
            routerTabs.tabChangeCallback = null
        }
    }

    CompositionLocalProvider(LocalComposeNavigatorTabsStateHolder providesDefault rememberSaveableStateHolder())
    {
        val navigatorParent = LocalComposeNavigator.currentOrThrow
        val navigatorTabs = rememberComposeNavigatorTabs(key)
        val navigator = rememberComposeNavigator(key + selectedTab,
            navigatorTabs.retrieveBackEntries(selectedTab) ?: listOf(),
            ComposeNavigatorViewModel.getInstance(viewModelStore!!.viewModelStore))

        DisposableEffect(navigatorTabs)
        {
            routerTabs.bindExecutor(CommandExecutorCompose(navigatorParent, hoster, null))

            onDispose {
                routerTabs.unbindExecutor()
            }
        }

        DisposableEffect(navigator)
        {
            router.bindExecutor(executorFactory.onCreate(navigator))

            onDispose {
                router.unbindExecutor()
                navigatorTabs.saveBackEntries(selectedTab, navigator.getStackEntriesSaveable())
            }
        }

        CompositionLocalProvider(LocalComposeNavigator provides navigator,
            LocalRouter provides router,
            LocalRouterTabs provides routerTabs)
        {
            content(router, navigator, navigatorTabs.lastTab, selectedTab)
            navigatorTabs.lastTab = selectedTab
        }
    }
}

class ComposeNavigatorTabs(val key: String,
                           backStackMap: Map<String, List<StackEntrySaveable>>)
{
    val backStackMap = mutableMapOf<String, List<StackEntrySaveable>>()
    var lastTab = 0

    init
    {
        this.backStackMap.putAll(backStackMap)
    }

    fun saveBackEntries(index: Int, states: List<StackEntrySaveable>)
    {
        backStackMap[index.toString()] = states
    }

    fun retrieveBackEntries(index: Int): List<StackEntrySaveable>? = backStackMap[index.toString()]
}