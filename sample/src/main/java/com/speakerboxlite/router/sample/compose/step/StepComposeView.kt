package com.speakerboxlite.router.sample.compose.step

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.sample.base.BaseViewCompose

class StepCompose(val step: Int): BaseViewCompose()
{
    val counter = mutableStateOf(0)

    @Composable
    override fun Root()
    {
        StepComposeView(step = step, counter = counter)
    }
}

@Composable
fun StepComposeView(step: Int, counter: MutableState<Int>)
{
    Surface {
        val router = LocalRouter.currentOrThrow

        Column {
            Row {
                Button(onClick = { router.route(StepComposePath(step + 1)) }) {
                    Text(text = "Next step $step")
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