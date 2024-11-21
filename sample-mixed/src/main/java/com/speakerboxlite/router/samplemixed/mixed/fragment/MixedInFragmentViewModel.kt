package com.speakerboxlite.router.samplemixed.mixed.fragment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.samplemixed.base.BaseViewModel
import com.speakerboxlite.router.samplemixed.step.compose.StepComposePath
import javax.inject.Inject
import javax.inject.Named

class MixedInFragmentViewModel(app: Application): BaseViewModel(app)
{
    @Inject
    @Named("mixedFragment")
    lateinit var mixedFragment: MutableLiveData<Int>

    var step = 0

    fun onSendToRoot()
    {
        step += 1
        resultProvider.send(step)
    }

    fun onSendToDi()
    {
        mixedFragment.value = mixedFragment.value!! + 1
    }

    fun onShowStepScreen()
    {
        router.route(StepComposePath(step))
    }
}