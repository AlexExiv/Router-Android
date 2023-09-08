package com.speakerboxlite.router.sample.composite.details

import android.app.Application
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.step.StepPath

class DetailsViewModel(app: Application): BaseViewModel(app)
{
    fun onShowStep()
    {
        router.route(StepPath(1), Presentation.Push)
    }

    fun onClose()
    {
        router.close()
    }
}