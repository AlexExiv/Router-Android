package com.speakerboxlite.router.samplemixed.mixed

import android.app.Application
import androidx.compose.runtime.Stable
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.samplemixed.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Named

@Stable
class MixedComposeViewModel(app: Application): BaseViewModel(app)
{
    @Inject
    @Named("mixedFragment")
    lateinit var mixedFragment: MutableLiveData<Int>

    @Inject
    @Named("mixedCompose")
    lateinit var mixedCompose: MutableLiveData<Int>

    val showBlock = MutableStateFlow(true)
    val fragmentStep = MutableStateFlow(0)
    val composeStep = MutableStateFlow(0)

    fun onDispatchFragmentResult(step: Int)
    {
        fragmentStep.value = step
    }

    fun onDispatchComposeResult(step: Int)
    {
        composeStep.value = step
    }
}