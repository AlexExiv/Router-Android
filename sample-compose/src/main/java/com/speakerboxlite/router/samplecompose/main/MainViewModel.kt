package com.speakerboxlite.router.samplecompose.main

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.speakerboxlite.router.samplecompose.base.BaseViewModel
import com.speakerboxlite.router.samplecompose.result.ResultPath

class MainViewModel(app: Application): BaseViewModel(app)
{
    var resultText by mutableStateOf("")

    fun onShowResult()
    {
        router.routeWithResult(this, ResultPath()) {
            it.vr.resultText = it.result //it's forbidden to use `this` context in the closure use only `it` data
        }
    }
}