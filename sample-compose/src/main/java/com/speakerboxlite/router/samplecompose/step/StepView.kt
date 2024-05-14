package com.speakerboxlite.router.samplecompose.step

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.samplecompose.base.BaseViewCompose
import com.speakerboxlite.router.samplecompose.bts.BottomSheetPath
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.compose.routerViewModel
import com.speakerboxlite.router.samplecompose.dialog.DialogPath

class StepView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Step(viewModel = routerViewModel())
    }
}

@Composable
fun Step(viewModel: StepViewModel)
{
    Surface(modifier = Modifier.fillMaxSize()) {
        val router = LocalRouter.currentOrThrow
        val step = viewModel.stepStr.observeAsState()
        val counter = viewModel.counter.observeAsState()
        val lockBack = viewModel.lockBackTitle.observeAsState()

        Row(modifier = Modifier.fillMaxSize()) {
            Column {
                Button(onClick = { viewModel.onNext() }) {
                    Text(text = "Next step ${step.value}")
                }

                Button(onClick = { viewModel.onIncCounter() }) {
                    Text(text = "Fake button ${counter.value}")
                }

                Button(onClick = { viewModel.onLockBack() }) {
                    Text(text = lockBack.value!!)
                }

                Button(onClick = { router.route(DialogPath()) }) {
                    Text(text = "Show dialog")
                }

                Button(onClick = { router.route(BottomSheetPath()) }) {
                    Text(text = "Show bottom sheet")
                }

                Button(onClick = { viewModel.onCloseAndShow() }) {
                    Text(text = "Close and show")
                }

                Button(onClick = { viewModel.onCloseToRoot() }) {
                    Text(text = "Close root")
                }
            }
        }
    }
}