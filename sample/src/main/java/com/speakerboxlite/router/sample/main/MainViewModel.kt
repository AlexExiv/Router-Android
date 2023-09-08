package com.speakerboxlite.router.sample.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.composite.CompositePath
import com.speakerboxlite.router.sample.dialogs.DialogPath
import com.speakerboxlite.router.sample.step.StepPath
import com.speakerboxlite.router.sample.tabs.TabsRoute

class MainViewModel(app: Application): BaseViewModel(app)
{
    val dialogResult = MutableLiveData("")

    fun onShowStep()
    {
        router.route(StepPath(0), Presentation.Push)
    }

    fun onShowTabs()
    {
        router.route(TabsRoute(), Presentation.Push)
    }

    fun onShowComposite()
    {
        router.route(CompositePath(), Presentation.Push)
    }

    fun onShowDialog()
    {
        router.routeDialogWithResult<Boolean>(DialogPath(title = "Title", message = "Tap Yes or No", okBtn = "Yes", cancelBtn = "No")) {
            dialogResult.value = "Dialog result: ${if (it) "Yes" else "No"}"
        }
    }
}