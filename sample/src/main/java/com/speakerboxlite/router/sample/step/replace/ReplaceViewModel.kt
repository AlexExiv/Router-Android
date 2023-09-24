package com.speakerboxlite.router.sample.step.replace

import android.app.Application
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.step.StepPath

class ReplaceViewModel(val step: Int, app: Application): BaseViewModel(app)
{
    val message = "I was called from step #$step"

    fun onReplaceBack()
    {
        router.replace(StepPath(step))
    }
}