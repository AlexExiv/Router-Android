package com.speakerboxlite.router.result

import com.speakerboxlite.router.RouterResultDispatcher
import com.speakerboxlite.router.ViewResult

enum class ViewResultType
{
    View, ViewModel;

    companion object
    {
        fun fromViewResult(vr: ViewResult): ViewResultType =
            when (vr)
            {
                is com.speakerboxlite.router.View -> View
                is com.speakerboxlite.router.ViewModel -> ViewModel
                else -> error("Unknown view result type")
            }
    }
}

interface ResultManager
{
    fun register(to: String, provider: RouterResultProvider)
    fun unregister(to: String, resultType: ViewResultType)

    fun bind(from: String, to: String, resultType: ViewResultType, result: RouterResultDispatcher<ViewResult, Any>?)
    fun bind(originalFrom: String, from: String)
    fun unbind(from: String)
    fun send(from: String, result: Any)
}