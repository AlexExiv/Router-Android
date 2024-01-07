package com.speakerboxlite.router.fragmentcompose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.compose.ComposeNavigatorContent
import com.speakerboxlite.router.compose.ComposeNavigatorTabs
import com.speakerboxlite.router.compose.ComposeNavigatorTabsContent
import com.speakerboxlite.router.compose.ComposeViewHoster
import com.speakerboxlite.router.compose.CurrentScreen
import com.speakerboxlite.router.compose.CurrentScreenTab
import com.speakerboxlite.router.compose.StackEntry
import com.speakerboxlite.router.compose.compositionUniqueId
import com.speakerboxlite.router.compose.contentTransformChangeTab
import com.speakerboxlite.router.compose.currentOrThrow

@Composable
fun ComposeNavigatorMixed(key: String = compositionUniqueId(),
                          router: Router,
                          hoster: ComposeViewHoster? = null,
                          hostCloseable: HostCloseable? = null,
                          fragmentHostFactory: HostComposeFragmentFactory,
                          content: ComposeNavigatorContent = { router, navigator -> CurrentScreen(router, navigator) })
{
    CompositionLocalProvider(LocalHostComposeFragmentFactory provides fragmentHostFactory)
    {
        ComposeNavigator(
            key = key,
            router = router,
            hoster = hoster,
            hostCloseable = hostCloseable,
            executorFactory = { CommandExecutorComposeMixed(it, hoster, hostCloseable) },
            content = content)
    }
}

@Composable
fun ComposeNavigatorTabsMixed(key: String = compositionUniqueId(),
                              routerTabs: RouterTabs,
                              tabPaths: List<RoutePath>,
                              selectedTab: Int,
                              hoster: ComposeViewHoster? = null,
                              hostCloseable: HostCloseable? = null,
                              fragmentHostFactory: HostComposeFragmentFactory = LocalHostComposeFragmentFactory.currentOrThrow,
                              transitionTabsSpec: AnimatedContentTransitionScope<StackEntry>.(prevTab: Int, currentTab: Int) -> ContentTransform = { p, c -> contentTransformChangeTab(p, c) },
                              onTabChanged: (Int) -> Unit,
                              content: ComposeNavigatorTabsContent = { router, navigator, lastTab, selectedTab -> CurrentScreenTab(router, navigator, lastTab, selectedTab, transitionTabsSpec) })
{
    CompositionLocalProvider(LocalHostComposeFragmentFactory provides fragmentHostFactory)
    {
        ComposeNavigatorTabs(
            key = key,
            routerTabs = routerTabs,
            tabPaths = tabPaths,
            selectedTab = selectedTab,
            hoster = hoster,
            hostCloseable = hostCloseable,
            transitionTabsSpec = transitionTabsSpec,
            executorFactory = { CommandExecutorComposeMixed(it, hoster, hostCloseable) },
            onTabChanged = onTabChanged,
            content = content)
    }
}