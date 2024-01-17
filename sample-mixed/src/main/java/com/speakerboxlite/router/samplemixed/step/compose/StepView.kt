package com.speakerboxlite.router.samplemixed.step.compose

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
import com.speakerboxlite.router.samplemixed.base.compose.BaseViewCompose
import com.speakerboxlite.router.samplemixed.bts.BottomSheetPath
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.compose.routerViewModel
import com.speakerboxlite.router.samplemixed.dialog.DialogPath
import com.speakerboxlite.router.samplemixed.dialog.fragment.DialogFragmentPath
import com.speakerboxlite.router.samplemixed.step.StepViewModel

class StepView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Step(viewModel = routerViewModel(view = this))
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
                Button(onClick = { viewModel.onNextCompose() }) {
                    Text(text = "Next step Compose ${step.value}")
                }

                Button(onClick = { viewModel.onNextFragment() }) {
                    Text(text = "Next step Fragment ${step.value}")
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

                Button(onClick = { router.route(DialogFragmentPath(message = "Im fragment", okBtn = "Close")) }) {
                    Text(text = "Show dialog fragment")
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