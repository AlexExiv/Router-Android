package com.speakerboxlite.router

typealias LogFun = (String, String) -> Unit

object RouterConfigGlobal
{
    var logFun: LogFun? = null
    var restoreSingleTime = true

    fun log(tag: String, message: String)
    {
        logFun?.invoke(tag, message)
    }
}