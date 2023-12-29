package com.speakerboxlite.router.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.command.CommandExecutorCompose
import com.speakerboxlite.router.command.ComposeViewHoster

typealias ComposeNavigatorTabsContent =
        @Composable (router: Router, navigator: ComposeNavigator, prevTab: Int?, currentTab: Int) -> Unit

@Composable
fun CurrentScreenTab(router: Router,
                     navigator: ComposeNavigator,
                     prevTab: Int?,
                     currentTab: Int,
                     transitionTabsSpec: AnimatedContentTransitionScope<StackEntry?>.(prevTab: Int?, currentTab: Int) -> ContentTransform)
{
    val stackEntry = navigator.lastItem

    val animation = remember(stackEntry) { AnimationControllerComposeSlide() }

    ComposeViewEffect(stackEntry = stackEntry, router = router)

    AnimatedContent(
        targetState = stackEntry,
        transitionSpec = {
            if (prevTab == null)
                ContentTransformNone
            else if (prevTab == currentTab)
                animation.prepareAnimation(navigator, this)
            else
                transitionTabsSpec(this, prevTab, currentTab)
        },
        label = "") { se ->

        CompleteTransitionEffect(stackEntry = se, navigator = navigator)

        se?.LocalOwnersProvider(navigator.stateHolder) { se.view.Root() }
    }
}

@Composable
fun ComposeNavigatorTabs(key: String = compositionUniqueId(),
                         routerTabs: RouterTabs,
                         selectedTab: Int,
                         hoster: ComposeViewHoster? = null,
                         transitionTabsSpec: AnimatedContentTransitionScope<StackEntry?>.(prevTab: Int?, currentTab: Int) -> ContentTransform = { _, _ -> ContentTransformNone },
                         content: ComposeNavigatorTabsContent = { router, navigator, prevTab, currentTab -> CurrentScreenTab(router, navigator, prevTab, currentTab, transitionTabsSpec) })
{
    val viewModelStore = LocalViewModelStoreOwner.current
    val router = routerTabs[selectedTab]

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
            router.bindExecutor(CommandExecutorCompose(navigator, hoster, null))

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
    var lastTab: Int? = null

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