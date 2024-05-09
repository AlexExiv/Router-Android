package com.speakerboxlite.router.samplehilt.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.result.RouterResultProvider

open class BaseViewModel(app: Application): AndroidViewModel(app), ViewModel
{
    override lateinit var router: Router
    override lateinit var resultProvider: RouterResultProvider

    override var isInit: Boolean = false

    override fun onInit()
    {

    }
}