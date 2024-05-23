package com.speakerboxlite.router.samplecompose.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.bootstrap.BaseViewCompose
import com.speakerboxlite.router.compose.routerViewModel

class ResultView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Result(viewModel = routerViewModel(view = this))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Result(viewModel: ResultViewModel)
{
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column {
                OutlinedTextField(
                    value = viewModel.text,
                    onValueChange = { viewModel.text = it },
                    label = { Text("Label") })

                Button(onClick = { viewModel.onSend() }) {
                    Text(text = "Send")
                }
            }
        }
    }
}