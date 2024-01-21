package com.speakerboxlite.router.samplemixed.main

import android.app.Application
import com.speakerboxlite.router.samplemixed.base.BaseViewModel
import com.speakerboxlite.router.samplemixed.step.compose.StepComposePath
import com.speakerboxlite.router.samplemixed.step.fragment.StepFragmentPath
import com.speakerboxlite.router.samplemixed.tabs.TabsPath

class MainViewModel(app: Application): BaseViewModel(app)
{
    fun onShowComposeStep()
    {
        router.route(StepComposePath(0))
    }

    fun onShowFragmentStep()
    {
        router.route(StepFragmentPath(0))
    }

    fun onShowStepUrl()
    {
        router.route("/steps/15")
    }

    fun onShowTabs()
    {
        router.route(TabsPath())
    }
}