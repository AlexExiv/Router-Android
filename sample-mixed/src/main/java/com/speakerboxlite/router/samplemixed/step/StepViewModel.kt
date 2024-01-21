package com.speakerboxlite.router.samplemixed.step

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.samplemixed.base.BaseViewModel
import com.speakerboxlite.router.samplemixed.step.compose.StepComposePath
import com.speakerboxlite.router.samplemixed.step.fragment.StepFragmentPath
import com.speakerboxlite.router.samplemixed.tabs.TabsPath
import com.speakerboxlite.router.samplemixed.tabs.tab.TabPath1

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

    fun onNextCompose()
    {
        if (step < 4)
            router.route(StepComposePath(step + 1), Presentation.Push)
        else
            router.route(StepComposePath(step + 10), Presentation.Modal)
    }

    fun onNextFragment()
    {
        if (step < 4)
            router.route(StepFragmentPath(step + 1), Presentation.Push)
        else
            router.route(StepFragmentPath(step + 10), Presentation.Modal)
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
        router.close()?.route(StepComposePath(step + 1000))
    }
}