package com.speakerboxlite.router.pattern

interface UrlMatcher
{
    val pattern: String
    val parameterNames: List<String>

    fun matches(url: String): Boolean
    fun match(url: String): UrlMatch?
}