package com.speakerboxlite.router.result

import com.speakerboxlite.router.Result
import com.speakerboxlite.router.RouterResultDispatcher
import com.speakerboxlite.router.ViewResult

internal data class ResultConnector(val from: String,
                                    val to: String,
                                    val resultType: ViewResultType,
                                    val result: RouterResultDispatcher<ViewResult, Any>?)

internal data class ResultSave(val from: String,
                               val result: Any)

class ResultManagerImpl: ResultManager
{
    private val connectors = mutableMapOf<String, ResultConnector>()
    private val providers = mutableMapOf<String, MutableMap<ViewResultType, RouterResultProvider>>()
    private val resultsPool = mutableMapOf<String, MutableList<ResultSave>>()

    private val maxPostponed = 3
    private val postponedUnbinding = mutableListOf<String>()

    override fun register(to: String, provider: RouterResultProvider)
    {
        if (providers[to] == null)
            providers[to] = mutableMapOf()

        val vrt = provider.resultType ?: error("")
        providers[to]!![vrt] = provider
        sendResults(vrt, to)
    }

    override fun unregister(to: String, resultType: ViewResultType)
    {
        providers[to]?.remove(resultType)

        if (providers[to]?.isEmpty() == true)
            providers.remove(to)
    }

    override fun bind(from: String, to: String, resultType: ViewResultType, result: RouterResultDispatcher<ViewResult, Any>?)
    {
        connectors[from] = ResultConnector(from, to, resultType, result)
    }

    override fun bind(originalFrom: String, from: String)
    {
        val connector = connectors[originalFrom] ?: return
        connectors[from] = connector.copy(from = from)
    }

    override fun unbind(from: String)
    {
        if (!postponedUnbinding.contains(from))
            postponedUnbinding.add(from)

        executePostponed()
    }

    override fun send(from: String, result: Any)
    {
        val connector = connectors[from] ?: return
        val resultDispatcher = connector.result ?: return
        val provider = providers[connector.to]?.get(connector.resultType)
        val vr = provider?.viewResult

        if (provider == null || vr == null)
            saveResult(connector.to, from, result)
        else
            resultDispatcher.onDispatch(Result(vr, result))
    }

    private fun saveResult(to: String, from: String, result: Any)
    {
        if (resultsPool[to] == null)
            resultsPool[to] = mutableListOf()

        resultsPool[to]!!.add(ResultSave(from, result))
    }

    private fun sendResults(resultType: ViewResultType, to: String)
    {
        val results = resultsPool[to] ?: return
        val provider = providers[to]?.get(resultType) ?: return
        val vr = provider.viewResult ?: return

        var i = 0
        while (i < results.size)
        {
            val r = results[i]
            val connector = connectors[r.from] ?: continue

            if (connector.resultType == resultType)
            {
                connector.result?.onDispatch(Result(vr, r.result))
                results.removeAt(i)
            }
            else
                i += 1
        }

        resultsPool[to] = results
    }

    private fun executePostponed()
    {
        while (postponedUnbinding.size > maxPostponed)
        {
            val first = postponedUnbinding.removeFirst()
            connectors.remove(first)
            providers.remove(first)
            resultsPool.remove(first)
        }
    }
}