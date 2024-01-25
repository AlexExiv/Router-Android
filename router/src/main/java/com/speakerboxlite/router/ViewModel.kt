package com.speakerboxlite.router

interface ViewModel: ViewResult
{
    var isInit: Boolean
    var router: Router

    fun onInit()
}