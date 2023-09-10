package com.speakerboxlite.router.result

interface RouterResultProvider
{
    fun <R: Any> send(result: R)

    fun start()
    fun pause()
}