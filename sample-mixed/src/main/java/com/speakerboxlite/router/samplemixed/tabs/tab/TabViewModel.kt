package com.speakerboxlite.router.samplemixed.tabs.tab

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.samplemixed.base.BaseViewModel
import com.speakerboxlite.router.samplemixed.di.modules.UserData
import com.speakerboxlite.router.samplemixed.step.compose.StepComposePath
import com.speakerboxlite.router.samplemixed.step.fragment.StepFragmentPath

class TabViewModel(val index: Int, app: Application): BaseViewModel(app)
{
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
        router.route(StepFragmentPath(0))
    }

    fun onNextCompose()
    {
        router.route(StepComposePath(0))
    }

    fun onSingleton()
    {
        router.route(TabPath1())
    }

    fun onLockBack()
    {
        lockBack = !lockBack
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