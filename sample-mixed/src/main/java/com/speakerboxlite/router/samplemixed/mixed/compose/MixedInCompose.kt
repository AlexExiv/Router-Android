package com.speakerboxlite.router.samplemixed.mixed.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.speakerboxlite.router.compose.bootstrap.BaseViewCompose
import com.speakerboxlite.router.compose.routerViewModel

class MixedInComposeView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        MixedInCompose(viewModel = routerViewModel())
    }
}

@Composable
fun MixedInCompose(viewModel: MixedInComposeViewModel)
{
    Column(modifier = Modifier.background(Color.LightGray)) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = "I'm a mixed in compose text")

        Button(onClick = { viewModel.onSendToRoot() }) {
            Text(text = "Send to root")
        }

        Button(onClick = { viewModel.onSendToDi() }) {
            Text(text = "Send to root DI")
        }

        Button(onClick = { viewModel.onShowStepScreen() }) {
            Text(text = "Show Step Screen")
        }
    }

}