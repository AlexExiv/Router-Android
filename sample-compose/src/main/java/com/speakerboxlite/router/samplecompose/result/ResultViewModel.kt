package com.speakerboxlite.router.samplecompose.result

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.speakerboxlite.router.samplecompose.base.BaseViewModel

class ResultViewModel(app: Application): BaseViewModel(app)
{
    var text by mutableStateOf("")

    fun onSend()
    {
        router.close()
        resultProvider.send(text)
    }
}