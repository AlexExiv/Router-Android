package com.speakerboxlite.router.samplemixed.tabs.tab.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.bootstrap.BaseViewCompose
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.samplemixed.step.compose.StepComposePath

class TabView(val tabNum: Int): BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Tab(tabNum = tabNum)
    }
}

@Composable
fun Tab(tabNum: Int)
{
    val router = LocalRouter.currentOrThrow

    Surface(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize()){
            Row {
                Text(text = "Tab #$tabNum")
            }

            Row {
                Button(onClick = { router.route(StepComposePath(0)) }) {
                    Text(text = "Show steps")
                }
            }
        }
    }
}