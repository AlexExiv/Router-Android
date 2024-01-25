package com.speakerboxlite.router.samplecompose.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.samplecompose.base.BaseViewCompose
import com.speakerboxlite.router.samplecompose.bts.BottomSheetPath
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.compose.routerViewModel
import com.speakerboxlite.router.samplecompose.dialog.DialogPath
import com.speakerboxlite.router.samplecompose.result.ResultPath
import com.speakerboxlite.router.samplecompose.step.StepAuthPath
import com.speakerboxlite.router.samplecompose.step.StepPath
import com.speakerboxlite.router.samplecompose.tabs.TabsPath

class MainView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Main(viewModel = routerViewModel(view = this))
    }
}

@Composable
fun Main(viewModel: MainViewModel)
{
    Surface(modifier = Modifier.fillMaxSize()) {
        val router = LocalRouter.currentOrThrow

        Row(modifier = Modifier.fillMaxSize()) {
            Column {
                Button(onClick = { router.route(StepPath(0)) }) {
                    Text(text = "Show steps")
                }

                Button(onClick = { router.route(StepAuthPath(0)) }) {
                    Text(text = "Show auth steps")
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

                Button(onClick = { viewModel.onShowResult() }) {
                    Text(text = if (viewModel.resultText.isEmpty()) "Show result" else "Last result: ${viewModel.resultText}")
                }
            }
        }
    }
}