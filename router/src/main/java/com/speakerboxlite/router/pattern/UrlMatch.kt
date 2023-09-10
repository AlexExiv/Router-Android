package com.speakerboxlite.router.pattern

class UrlMatch(val parameters: Map<String, String>)
{
    operator fun get(name: String): String? = parameters[name]

    fun parameterSet(): Set<Map.Entry<String, String>> = parameters.entries
}