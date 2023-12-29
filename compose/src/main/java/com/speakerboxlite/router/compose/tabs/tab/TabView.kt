package com.speakerboxlite.router.compose.tabs.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.base.BaseViewCompose
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.compose.step.StepPath

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

    Surface {
        Column {
            Row {
                Text(text = "Tab #$tabNum")
            }

            Row {
                Button(onClick = { router.route(StepPath(0)) }) {
                    Text(text = "Show steps")
                }
            }
        }
    }
}