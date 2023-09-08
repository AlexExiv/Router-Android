package com.speakerboxlite.router

data class ResultConnector(val from: String,
                           val to: String,
                           val result: Result<Any>)

class ResultManagerImpl: ResultManager
{
    val connectors = mutableMapOf<String, ResultConnector>()

    override fun bind(from: String, to: String, result: Result<Any>)
    {
        connectors[from] = ResultConnector(from, to, result)
    }

    override fun unbind(from: String)
    {
        connectors.remove(from)
    }

    override fun send(from: String, result: Any)
    {
        connectors[from]!!.result(result)
    }
}