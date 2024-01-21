package com.speakerboxlite.router.sample.shared

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.shared.subs.sub0.SharedSub0Path
import com.speakerboxlite.router.sample.shared.subs.sub1.SharedSub1Path
import javax.inject.Inject
import javax.inject.Named

class SharedViewModel(app: Application): BaseViewModel(app)
{
    @Inject
    @Named("sharedVar0")
    lateinit var sharedVar0: MutableLiveData<String>

    @Inject
    @Named("sharedVar1")
    lateinit var sharedVar1: MutableLiveData<String>

    fun onShowSub0()
    {
        router.route(SharedSub0Path())
    }

    fun onShowSub1()
    {
        router.route(SharedSub1Path())
    }

    fun onShowShared()
    {
        router.route(SharedPath())
    }

    fun onClose()
    {
        router.close()
    }
}
