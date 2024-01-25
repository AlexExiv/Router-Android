package com.speakerboxlite.router.zombie

import com.speakerboxlite.router.ViewResult
import com.speakerboxlite.router.result.RouterResultProvider
import com.speakerboxlite.router.result.ViewResultType

internal class RouterResultProviderZombie : RouterResultProvider
{
    override val key: String get() = TODO("Not yet implemented")
    override val viewResult: ViewResult? get() = TODO("Not yet implemented")
    override val resultType: ViewResultType? get() = TODO("Not yet implemented")

    override fun <R : Any> send(result: R)
    {

    }

    override fun start(vke: ViewResult)
    {

    }

    override fun pause()
    {

    }
}