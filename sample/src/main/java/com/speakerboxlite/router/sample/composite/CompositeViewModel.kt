package com.speakerboxlite.router.sample.composite

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.base.BaseViewModel
import javax.inject.Inject
import javax.inject.Named

class CompositeViewModel(app: Application): BaseViewModel(app)
{
    @Inject
    @Named("sharedVar")
    lateinit var sharedVar: MutableLiveData<String>

    val resultVal = MutableLiveData("")

    fun onChangeValue(value: String)
    {
        resultVal.value = "This is random value from result: $value"
    }
}