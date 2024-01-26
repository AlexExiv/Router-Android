package com.speakerboxlite.router.sample.tabs.tab

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.di.modules.UserData
import com.speakerboxlite.router.sample.step.StepPath
import javax.inject.Inject

class TabViewModel(val index: Int, app: Application): BaseViewModel(app)
{
    @Inject
    lateinit var userData: UserData

    val indexStr = MutableLiveData(index.toString())

    var lockBack = false
        set(value)
        {
            field = value
            lockBackTitle.value = if (value) "Lock Back ON" else "Lock Back OFF"
            router.lockBack = value
        }

    val lockBackTitle = MutableLiveData("")

    fun onNext()
    {
        router.route(StepPath(0))
    }

    fun onSingleton()
    {
        router.route(TabPath1())
    }

    fun onLockBack()
    {
        lockBack = !lockBack
    }

    fun onShowStepPresent()
    {
        router.route(StepPath(0), Presentation.ModalNewTask)
    }

    fun onResetAuth()
    {
        userData.isLogin.value = false
    }

    fun onResetPro()
    {
        userData.isPro.value = false
    }
}