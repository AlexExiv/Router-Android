package com.speakerboxlite.router.compose.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.base.BaseViewCompose
import com.speakerboxlite.router.compose.bts.BottomSheetPath
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.compose.dialog.DialogPath
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
    Surface(modifier = Modifier.fillMaxSize()) {
        val router = LocalRouter.currentOrThrow

        Row(modifier = Modifier.fillMaxSize()) {
            Column {
                Button(onClick = { router.route(StepPath(0)) }) {
                    Text(text = "Show steps")
                }

                Button(onClick = { router.route(TabsPath()) }) {
                    Text(text = "Show tabs")
                }

                Button(onClick = { router.route(DialogPath()) }) {
                    Text(text = "Show dialog")
                }

                Button(onClick = { router.route(BottomSheetPath()) }) {
                    Text(text = "Show bottom sheet")
                }
            }
        }
    }
}