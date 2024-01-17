package com.speakerboxlite.router.samplemixed.dialog.fragment

import android.app.Application
import com.speakerboxlite.router.samplemixed.base.BaseViewModel

class DialogViewModel(val title: String,
                      val message: String,
                      val okBtn: String,
                      val cancelBtn: String,
                      app: Application): BaseViewModel(app)
{
    fun onOk()
    {
        resultProvider.send(true)
        router.close()
    }

    fun onCancel()
    {
        resultProvider.send(false)
        router.close()
    }

    fun onShowAnotherDialog()
    {
        router.routeDialog(DialogFragmentPath("I'm a additional dialog", "", "Close"))
    }
}