package com.speakerboxlite.router.sample.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.ViewModel

open class BaseViewModel(app: Application): AndroidViewModel(app), ViewModel
{
    override lateinit var router: Router

    var isInit: Boolean = false
        private set

    fun onInitRequested()
    {
        isInit = true
    }

    open fun onInit()
    {

    }
}