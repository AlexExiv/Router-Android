package com.speakerboxlite.router.sample.step

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.tabs.tab.TabSingletonPath

class StepViewModel(val step: Int, app: Application): BaseViewModel(app)
{
    val stepStr = MutableLiveData(step.toString())

    override fun onInit()
    {
        super.onInit()
    }

    fun onNext()
    {
        if (step < 4)
            router.route(StepPath(step + 1), Presentation.Push)
        else
            router.route(StepPath(step*10), Presentation.Modal)
    }

    fun onCloseToRoot()
    {
        router.closeToTop()
    }

    fun onShowSingleton()
    {
        router.route(TabSingletonPath(100))
    }
}