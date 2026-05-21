package com.speakerboxlite.router.sample.legacy

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.di.modules.AppData
import com.speakerboxlite.router.sample.legacy.fragmentfactory.LegacyFragmentFactoryPath
import com.speakerboxlite.router.sample.legacy.fragmentroute.LegacyFragmentRoutePath
import javax.inject.Inject

class SimpleIntegrationRootViewModel(app: Application): BaseViewModel(app)
{
    val injected = MutableLiveData("")

    @Inject
    lateinit var appData: AppData

    override fun onInit()
    {
        super.onInit()
        injected.value = "Injected AppData in ViewModel: ${appData.appString}"
    }

    fun onShowFragmentRoute()
    {
        router.route(LegacyFragmentRoutePath(
            title = "RouterFragmentRoute from ViewModel",
            source = "SimpleIntegrationRootViewModel routed this path"))
    }

    fun onShowFragmentFactory()
    {
        router.route(LegacyFragmentFactoryPath(
            title = "RouterFragment from ViewModel",
            source = "SimpleIntegrationRootViewModel routed this path"))
    }
}
