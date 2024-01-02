package com.speakerboxlite.router.compose.bts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.ViewBTS
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.base.BaseViewCompose
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.compose.dialog.DialogPath
import com.speakerboxlite.router.compose.step.StepPath

class BottomSheetView: BaseViewCompose(), ViewBTS
{
    @Composable
    override fun Root()
    {
        BottomSheet()
    }

}

@Composable
fun BottomSheet()
{
    val router = LocalRouter.currentOrThrow

    Surface(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "I'm a Bottom sheet")

                Button(onClick = { router.route(StepPath(0)) }) {
                    Text(text = "Show Steps")
                }

                Button(onClick = { router.route(DialogPath()) }) {
                    Text(text = "Show Dialog")
                }

                Button(onClick = { router.route(BottomSheetPath()) }) {
                    Text(text = "Show Bottom Sheet")
                }

                Button(onClick = { router.close() }) {
                    Text(text = "Close")
                }
            }

        }
    }
}