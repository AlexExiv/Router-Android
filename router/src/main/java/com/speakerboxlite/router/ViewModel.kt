package com.speakerboxlite.router

import com.speakerboxlite.router.result.RouterResultProvider

interface ViewModel
{
    val isInit: Boolean
    var router: Router
    var resultProvider: RouterResultProvider
}