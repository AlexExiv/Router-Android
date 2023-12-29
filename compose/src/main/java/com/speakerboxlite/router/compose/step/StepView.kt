package com.speakerboxlite.router.compose.step

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.base.BaseViewCompose
import com.speakerboxlite.router.compose.currentOrThrow

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
    Surface {
        val router = LocalRouter.currentOrThrow

        Column {
            Row {
                Button(onClick = { router.route(StepPath(step.value + 1)) }) {
                    Text(text = "Next step ${step.value}")
                }
            }

            Row {
                Button(onClick = { router.route(StepPath(step.value + 1), Presentation.Modal) }) {
                    Text(text = "Next step ${step.value} Modal")
                }
            }

            Row {
                Button(onClick = { counter.value += 1 }) {
                    Text(text = "Fake button ${counter.value}")
                }
            }
        }
    }
}