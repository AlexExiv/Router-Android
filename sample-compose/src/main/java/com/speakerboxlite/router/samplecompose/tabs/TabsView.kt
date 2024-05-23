package com.speakerboxlite.router.samplecompose.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.ComposeNavigatorTabs
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.bootstrap.BaseViewCompose
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.samplecompose.tabs.tab.TabPath0
import com.speakerboxlite.router.samplecompose.tabs.tab.TabPath1
import com.speakerboxlite.router.samplecompose.tabs.tab.TabPath2

class TabsView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Tabs(viewKey)
    }
}

@Composable
fun Tabs(key: String)
{
    Surface {

        // create RouterTabs for this view
        val routerTabs = LocalRouter.currentOrThrow.createRouterTabs(key)

        Column(modifier = Modifier.fillMaxSize()) {

            // current page use routerTabs.tabIndex as the start value
            val page = remember { mutableIntStateOf(routerTabs.tabIndex) }

            // change pages only via routerTabs.route(0) don't change pages directly
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

            ComposeNavigatorTabs(
                routerTabs = routerTabs,
                tabPaths = listOf(TabPath0(), TabPath1(), TabPath2()),
                selectedTab = page.intValue,
                onTabChanged = { page.intValue = it })
        }
    }
}
