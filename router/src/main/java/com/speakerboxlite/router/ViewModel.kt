package com.speakerboxlite.router

import com.speakerboxlite.router.result.RouterResultProvider

interface ViewModel
{
    var isInit: Boolean
    var router: Router
    var resultProvider: RouterResultProvider

    fun onInit()
}