package com.speakerboxlite.router.samplemixed.mixed.compose

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.samplemixed.base.BaseViewModel
import com.speakerboxlite.router.samplemixed.step.compose.StepComposePath
import javax.inject.Inject
import javax.inject.Named

class MixedInComposeViewModel(app: Application): BaseViewModel(app)
{
    @Inject
    @Named("mixedCompose")
    lateinit var mixedCompose: MutableLiveData<Int>

    var step = 0

    fun onSendToRoot()
    {
        step += 1
        resultProvider.send(step)
    }

    fun onSendToDi()
    {
        mixedCompose.value = mixedCompose.value!! + 1
    }

    fun onShowStepScreen()
    {
        router.route(StepComposePath(step))
    }
}