package com.speakerboxlite.router.pattern

class UrlMatch(parameters: Map<String, String>?)
{
    private val parameters: MutableMap<String, String> = HashMap()

    init
    {
        if (parameters != null)
            this.parameters.putAll(parameters)
    }

    operator fun get(name: String): String? = parameters[name]

    fun parameterSet(): Set<Map.Entry<String, String>> = parameters.entries
}