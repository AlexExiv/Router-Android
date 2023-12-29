package com.speakerboxlite.router.compose.tabs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.command.CommandExecutorCompose
import com.speakerboxlite.router.compose.CompleteTransitionEffect
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.compose.ComposeNavigatorContent
import com.speakerboxlite.router.compose.ComposeNavigatorTabs
import com.speakerboxlite.router.compose.ComposeViewEffect
import com.speakerboxlite.router.compose.ContentTransformNone
import com.speakerboxlite.router.compose.CurrentScreen
import com.speakerboxlite.router.compose.FragmentContainerView
import com.speakerboxlite.router.compose.LocalComposeNavigator
import com.speakerboxlite.router.compose.LocalMainScope
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.StackEntry
import com.speakerboxlite.router.compose.ViewCompose
import com.speakerboxlite.router.compose.base.BaseViewCompose
import com.speakerboxlite.router.compose.compositionUniqueId
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.compose.tabs.tab.TabPath0
import com.speakerboxlite.router.compose.tabs.tab.TabPath1
import com.speakerboxlite.router.compose.tabs.tab.TabPath2
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID

@Composable
fun HostView(index: Int, routers: List<Router>)
{

    
}

class TabsView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Tabs(viewKey)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Tabs(key: String)
{

    Surface {

        val mainScope = LocalMainScope.currentOrThrow
        val routerTabs = LocalRouter.currentOrThrow.createRouterTabs(key)
        val navigator = LocalComposeNavigator.currentOrThrow

        val routers = remember {
            routerTabs.route(0, TabPath0(), false)
            routerTabs.route(1, TabPath1(), false)
            routerTabs.route(2, TabPath2(), false)

            listOf(routerTabs[0], routerTabs[1], routerTabs[2])
        }

        Column(modifier = Modifier.fillMaxWidth()) {

            val pagerState = rememberPagerState { 3 }
            val page = remember { mutableIntStateOf(0) }

            TabRow(selectedTabIndex = page.intValue) {
                Tab(selected = page.intValue == 0, onClick = { routerTabs.route(0) }) {
                    Text(text = "Tab 0")
                }
                Tab(selected = page.intValue == 1, onClick = { routerTabs.route(1) }) {
                    Text(text = "Tab 1")
                }
                Tab(selected = page.intValue == 2, onClick = { routerTabs.route(2) }) {
                    Text(text = "Tab 2")
                }
            }

            DisposableEffect(key1 = routerTabs)
            {
                routerTabs.tabChangeCallback = { runBlocking { page.intValue = it }  }

                onDispose {
                    routerTabs.tabChangeCallback = null
                }
            }

            ComposeNavigatorTabs(routerTabs = routerTabs, selectedTab = page.intValue)
        }
    }
}
