package com.speakerboxlite.router.samplehilt.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.samplehilt.base.BaseViewCompose
import com.speakerboxlite.router.samplehilt.base.routerHiltViewModel
import com.speakerboxlite.router.samplehilt.nohilt.NoHiltPath
import com.speakerboxlite.router.samplehilt.step.StepAuthPath
import com.speakerboxlite.router.samplehilt.step.StepPath

class MainView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Main(viewModel = routerHiltViewModel(view = this))
    }
}

@Composable
fun Main(viewModel: MainViewModel)
{
    Surface(modifier = Modifier.fillMaxSize()) {
        val router = LocalRouter.currentOrThrow

        Row(modifier = Modifier.fillMaxSize()) {
            Column {
                Text(text = viewModel.authText)

                Button(onClick = { router.route(StepPath(0)) }) {
                    Text(text = "Show steps")
                }

                Button(onClick = { router.route(StepAuthPath(0)) }) {
                    Text(text = "Show auth steps")
                }

                Button(onClick = { router.route(NoHiltPath(0)) }) {
                    Text(text = "Show no Hilt view")
                }
            }
        }
    }
}