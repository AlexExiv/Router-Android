package com.speakerboxlite.router.sample.shared.subs.sub0

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.base.BaseViewModel
import javax.inject.Inject
import javax.inject.Named

class SharedSub0ViewModel(app: Application): BaseViewModel(app)
{
    @Inject
    @Named("sharedVar0")
    lateinit var sharedVar: MutableLiveData<String>

    fun onUpdateValue()
    {
        sharedVar.value = "SUB 0: ${(0..10).random()}"
    }
}