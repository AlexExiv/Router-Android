package com.speakerboxlite.router.result

import com.speakerboxlite.router.ViewResult

interface RouterResultProvider
{
    val key: String
    val viewResult: ViewResult?
    val resultType: ViewResultType?

    fun <R: Any> send(result: R)

    fun start(vke: ViewResult)
    fun pause()
}