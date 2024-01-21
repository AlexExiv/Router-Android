package com.speakerboxlite.router.samplecompose.step

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.samplecompose.base.BaseViewModel
import com.speakerboxlite.router.samplecompose.tabs.TabsPath
import com.speakerboxlite.router.samplecompose.tabs.tab.TabPath1

class StepViewModel(val step: Int, app: Application): BaseViewModel(app)
{
    val stepStr = MutableLiveData(step.toString())
    val counter = MutableLiveData(0)

    var lockBack = false
        set(value)
        {
            field = value
            lockBackTitle.value = if (value) "Lock Back ON" else "Lock Back OFF"
            router.lockBack = value
        }

    val lockBackTitle = MutableLiveData("")

    override fun onInit()
    {
        super.onInit()
        lockBack = false
    }

    fun onIncCounter()
    {
        counter.value = counter.value!! + 1
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
        router.route(TabPath1())
    }
/*
    fun onReplace()
    {
        router.replace(ReplacePath(step))
    }
*/
    fun onLockBack()
    {
        lockBack = !lockBack
    }

    fun onShowTabs()
    {
        router.route(TabsPath())
    }

    fun onCloseAndShow()
    {
        router.close()?.route(StepPath(step + 1000))
    }
}