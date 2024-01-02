package com.speakerboxlite.router.sample.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.ViewCompose
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.sample.base.BaseViewCompose
import com.speakerboxlite.router.sample.compose.step.StepComposePath
import java.util.UUID

class MainCompose: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        MainComposeView()
    }
}

@Composable
fun MainComposeView()
{
    Box(modifier = Modifier.background(Color.Red)) {
        val router = LocalRouter.currentOrThrow

        Row {
            Text(text = "Test text")
        }

        Row {
            Button(onClick = { router.route(StepComposePath(1)) }) {
                Text(text = "Show steps")
            }
        }

    }
}