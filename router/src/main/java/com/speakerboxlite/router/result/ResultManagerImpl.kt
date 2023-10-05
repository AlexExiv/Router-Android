package com.speakerboxlite.router.result

import com.speakerboxlite.router.Result

data class ResultConnector(val from: String,
                           val to: String,
                           val result: Result<Any>?)

class ResultManagerImpl: ResultManager
{
    val connectors = mutableMapOf<String, ResultConnector>()

    val maxPostponed = 3
    val postponedUnbinding = mutableListOf<String>()

    override fun bind(from: String, to: String, result: Result<Any>?)
    {
        connectors[from] = ResultConnector(from, to, result)
    }

    override fun unbind(from: String)
    {
        if (!postponedUnbinding.contains(from))
            postponedUnbinding.add(from)

        executePostponed()
    }

    override fun send(from: String, result: Any)
    {
        connectors[from]!!.result?.invoke(result)
    }

    private fun executePostponed()
    {
        while (postponedUnbinding.size > maxPostponed)
        {
            val first = postponedUnbinding.removeFirst()
            connectors.remove(first)
        }
    }
}