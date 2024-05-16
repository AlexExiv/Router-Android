package com.speakerboxlite.router.compose.bootstrap

import android.app.Application
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.result.RouterResultProvider

abstract class AndroidViewModel(app: Application): androidx.lifecycle.AndroidViewModel(app), ViewModel
{
    override lateinit var router: Router
    override lateinit var resultProvider: RouterResultProvider

    override var isInit: Boolean = false

    /**
     * This method will be called after dependency injection
     */
    override fun onInit()
    {

    }
}