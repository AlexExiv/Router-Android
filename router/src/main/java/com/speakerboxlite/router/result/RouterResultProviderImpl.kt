package com.speakerboxlite.router.result

import com.speakerboxlite.router.ViewResult
import java.lang.ref.WeakReference

class RouterResultProviderImpl(private val from: String,
                               private val resultManager: ResultManager): RouterResultProvider
{
    override val key: String get() = from
    override val viewResult: ViewResult? get() = weakVR.get()
    override var resultType: ViewResultType? = null

    private var weakVR = WeakReference<ViewResult>(null)

    override fun <R : Any> send(result: R)
    {
        resultManager.send(from, result)
    }

    override fun start(vke: ViewResult)
    {
        val rt = ViewResultType.fromViewResult(vke)
        check(resultType == null || (resultType != null && resultType == rt)) { "" }
        resultType = rt

        weakVR = WeakReference(vke)
        resultManager.register(from, this)
    }

    override fun pause()
    {
        resultManager.unregister(from, resultType!!)
    }
}