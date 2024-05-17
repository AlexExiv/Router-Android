package com.speakerboxlite.router.samplehilt.nohilt

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
import com.speakerboxlite.router.composehilt.routerHiltViewModel
import com.speakerboxlite.router.samplehilt.step.StepAuthPath
import com.speakerboxlite.router.samplehilt.step.StepPath

class NoHiltView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        NoHilt(viewModel = routerHiltViewModel())
    }
}

@Composable
fun NoHilt(viewModel: NoHiltViewModel)
{
    Surface(modifier = Modifier.fillMaxSize()) {
        val router = LocalRouter.currentOrThrow

        Row(modifier = Modifier.fillMaxSize()) {
            Column {
                Text(text = "I'm not a hilt ViewModel View")

                Button(onClick = { router.route(StepPath(0)) }) {
                    Text(text = "Show steps")
                }

                Button(onClick = { router.route(StepAuthPath(0)) }) {
                    Text(text = "Show auth steps")
                }
            }
        }
    }
}