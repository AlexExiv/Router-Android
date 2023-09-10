package com.speakerboxlite.router.sample.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.chain.ChainPath
import com.speakerboxlite.router.sample.composite.CompositePath
import com.speakerboxlite.router.sample.dialogs.DialogPath
import com.speakerboxlite.router.sample.step.StepPath
import com.speakerboxlite.router.sample.tabs.TabsPath

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
        router.routeWithResult<Int>(ChainPath(0)) {
            chainResult.value = "Chain has been finished at step $it"
        }
    }

    fun onShowDialog()
    {
        router.routeDialogWithResult<Boolean>(DialogPath(title = "Title", message = "Tap Yes or No", okBtn = "Yes", cancelBtn = "No")) {
            dialogResult.value = "Dialog result: ${if (it) "Yes" else "No"}"
        }
    }
}