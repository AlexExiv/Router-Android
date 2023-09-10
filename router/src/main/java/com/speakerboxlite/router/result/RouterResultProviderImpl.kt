package com.speakerboxlite.router.result

class RouterResultProviderImpl(private val from: String,
                               private val resultManager: ResultManager): RouterResultProvider
{
    private val results = mutableListOf<Any>()
    private var started: Boolean = false

    override fun <R : Any> send(result: R)
    {
        if (started)
            resultManager.send(from, result)
        else
            results.add(result)
    }

    override fun start()
    {
        started = true
        results.forEach { resultManager.send(from, it) }
        results.clear()
    }

    override fun pause()
    {
        started = false
    }
}