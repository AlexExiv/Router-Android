package com.speakerboxlite.router.samplehilt.step

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.samplehilt.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = StepViewModel.Factory::class)
class StepViewModel @AssistedInject constructor(
    @Assisted val step: Int,
    app: Application,
    val savedStateHandle: SavedStateHandle /* just in test purposes */): BaseViewModel(app)
{
    @AssistedFactory
    interface Factory
    {
        fun create(step: Int): StepViewModel
    }

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

    fun onLockBack()
    {
        lockBack = !lockBack
    }

    fun onCloseAndShow()
    {
        router.close()?.route(StepPath(step + 1000))
    }
}