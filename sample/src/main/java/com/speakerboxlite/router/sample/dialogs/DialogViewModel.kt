package com.speakerboxlite.router.sample.dialogs

import android.app.Application
import com.speakerboxlite.router.sample.base.BaseViewModel

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
}