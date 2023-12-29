package com.speakerboxlite.router.compose.main

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
import com.speakerboxlite.router.compose.tabs.TabsPath

class MainView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Main()
    }
}

@Composable
fun Main()
{
    Surface {
        val router = LocalRouter.currentOrThrow

        Column {
            Row {
                Button(onClick = { router.route(StepPath(0)) }) {
                    Text(text = "Show steps")
                }
            }
            Row {
                Button(onClick = { router.route(TabsPath()) }) {
                    Text(text = "Show tabs")
                }
            }
        }
    }
}