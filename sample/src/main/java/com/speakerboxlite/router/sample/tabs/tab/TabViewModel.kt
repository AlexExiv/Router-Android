package com.speakerboxlite.router.sample.tabs.tab

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.step.StepPath

class TabViewModel(val index: Int, app: Application): BaseViewModel(app)
{
    val indexStr = MutableLiveData(index.toString())

    fun onNext()
    {
        router.route(StepPath(0))
    }

    fun onSingleton()
    {
        router.route(TabSingletonPath(100))
    }
}