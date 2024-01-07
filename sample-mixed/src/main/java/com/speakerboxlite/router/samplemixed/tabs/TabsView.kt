package com.speakerboxlite.router.samplemixed.tabs

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
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.samplemixed.base.compose.BaseViewCompose
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.fragmentcompose.ComposeNavigatorTabsMixed
import com.speakerboxlite.router.samplemixed.tabs.tab.TabPath0
import com.speakerboxlite.router.samplemixed.tabs.tab.TabPath1
import com.speakerboxlite.router.samplemixed.tabs.tab.TabPath2

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

        val routerTabs = LocalRouter.currentOrThrow.createRouterTabs(key)

        Column(modifier = Modifier.fillMaxSize()) {

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

            ComposeNavigatorTabsMixed(
                routerTabs = routerTabs,
                tabPaths = listOf(TabPath0(), TabPath1(), TabPath2()),
                selectedTab = page.intValue,
                onTabChanged = { page.intValue = it })
        }
    }
}
