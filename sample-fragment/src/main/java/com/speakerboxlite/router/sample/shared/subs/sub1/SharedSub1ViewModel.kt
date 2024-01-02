package com.speakerboxlite.router.sample.shared.subs.sub1

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.base.BaseViewModel
import javax.inject.Inject
import javax.inject.Named

class SharedSub1ViewModel(app: Application): BaseViewModel(app)
{
    @Inject
    @Named("sharedVar1")
    lateinit var sharedVar: MutableLiveData<String>

    fun onUpdateValue()
    {
        sharedVar.value = "SUB 1: ${(0..10).random()}"
    }
}