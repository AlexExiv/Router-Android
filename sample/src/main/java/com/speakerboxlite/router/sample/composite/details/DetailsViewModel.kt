package com.speakerboxlite.router.sample.composite.details

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.composite.CompositePath
import com.speakerboxlite.router.sample.step.StepPath
import javax.inject.Inject
import javax.inject.Named

class DetailsViewModel(app: Application): BaseViewModel(app)
{
    @Inject
    @Named("sharedVar")
    lateinit var sharedVar: MutableLiveData<String>

    fun onGenerateResult()
    {
        resultProvider.send((0..100).random().toString())
    }

    fun onGenerateShared()
    {
        sharedVar.value = "This is random value from result: ${(0..100).random()}"
    }

    fun onShowStep()
    {
        router.route(StepPath(1), Presentation.Push)
    }

    fun onShowComposite()
    {
        router.route(CompositePath())
    }

    fun onClose()
    {
        router.close()
    }
}