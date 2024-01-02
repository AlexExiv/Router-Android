package com.speakerboxlite.router.compose.step

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.base.BaseViewCompose
import com.speakerboxlite.router.compose.bts.BottomSheetPath
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.compose.dialog.DialogPath

class StepView: BaseViewCompose()
{
    val step = mutableIntStateOf(0)
    val counter = mutableIntStateOf(0)

    @Composable
    override fun Root()
    {
        Step(step = step, counter = counter)
    }
}

@Composable
fun Step(step: MutableState<Int>, counter: MutableState<Int>)
{
    Surface(modifier = Modifier.fillMaxSize()) {
        val router = LocalRouter.currentOrThrow

        Row(modifier = Modifier.fillMaxSize()) {
            Column {
                Button(onClick = { router.route(StepPath(step.value + 1)) }) {
                    Text(text = "Next step ${step.value}")
                }

                Button(onClick = { router.route(StepPath(step.value + 1), Presentation.Modal) }) {
                    Text(text = "Next step ${step.value} Modal")
                }

                Button(onClick = { counter.value += 1 }) {
                    Text(text = "Fake button ${counter.value}")
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