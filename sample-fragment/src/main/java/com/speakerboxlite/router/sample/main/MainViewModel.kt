package com.speakerboxlite.router.sample.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.chain.ChainPath
import com.speakerboxlite.router.sample.composite.CompositePath
import com.speakerboxlite.router.sample.dialogs.DialogPath
import com.speakerboxlite.router.sample.shared.SharedPath
import com.speakerboxlite.router.sample.simple.SimplePath
import com.speakerboxlite.router.sample.simple.component.SimpleComponentPath
import com.speakerboxlite.router.sample.step.StepPath
import com.speakerboxlite.router.sample.subtabs.SubTabsPath
import com.speakerboxlite.router.sample.tabs.TabsPath
import com.speakerboxlite.router.sample.theme.ThemePath

class MainViewModel(app: Application): BaseViewModel(app)
{
    val chainResult = MutableLiveData("")
    val dialogResult = MutableLiveData("")

    fun onShowStep()
    {
        router.route(StepPath(0))
    }

    fun onShowStepUrl()
    {
        router.route("/steps/15")
    }

    fun onShowTabs()
    {
        router.route(TabsPath())
    }

    fun onShowComposite()
    {
        router.route(CompositePath())
    }

    fun onShowChain()
    {
        router.routeWithResult(this, ChainPath(0)) {
            it.vr.chainResult.value = "Chain has been finished at step ${it.result}"
        }
    }

    fun onShowDialog()
    {
        router.routeWithResult(this, DialogPath(title = "Title", message = "Tap Yes or No", okBtn = "Yes", cancelBtn = "No")) {
            it.vr.dialogResult.value = "Dialog result: ${if (it.result) "Yes" else "No"}"
        }
    }

    fun onShowSharedComponent()
    {
        router.route(SharedPath())
    }

    fun onShowSimple()
    {
        router.route(SimplePath("Text to argument"))
    }

    fun onShowSimpleComponent()
    {
        router.route(SimpleComponentPath())
    }

    fun onShowTheme()
    {
        router.route(ThemePath(), Presentation.ModalNewTask)
    }

    fun onShowLocalTabs()
    {
        router.route(SubTabsPath())
    }
}