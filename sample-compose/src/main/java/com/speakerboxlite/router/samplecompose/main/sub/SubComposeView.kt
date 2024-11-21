package com.speakerboxlite.router.samplecompose.main.sub

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.speakerboxlite.router.compose.bootstrap.BaseViewCompose
import com.speakerboxlite.router.compose.routerViewModel
import com.speakerboxlite.router.samplecompose.main.Main

class SubComposeView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        SubCompose()
    }
}

@Composable
fun SubCompose()
{
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "I'm a sub compose view",
        color = Color.Black,
        fontSize = 16.sp,
        textAlign = TextAlign.Center)
}