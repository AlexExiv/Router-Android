package com.speakerboxlite.router.pattern

import java.util.regex.Matcher
import java.util.regex.Pattern

class UrlPattern(private val urlPattern: String) : UrlMatcher
{
    private lateinit var compiledUrl: Pattern

    override val pattern: String get() = urlPattern.replaceFirst(URL_FORMAT_REGEX.toRegex(), "")
    override val parameterNames = mutableListOf<String>()

    init
    {
        compile()
    }

    override fun match(url: String): UrlMatch?
    {
        val matcher = compiledUrl.matcher(url)
        return if (matcher.matches()) UrlMatch(extractParameters(matcher)) else null
    }

    override fun matches(url: String): Boolean = match(url) != null

    private fun compile()
    {
        acquireParameterNames()
        var parsedPattern = urlPattern.replaceFirst(URL_FORMAT_REGEX.toRegex(), URL_FORMAT_MATCH_REGEX)
        parsedPattern = parsedPattern.replace(URL_PARAM_REGEX.toRegex(), URL_PARAM_MATCH_REGEX)
        compiledUrl = Pattern.compile(parsedPattern + URL_QUERY_STRING_REGEX)
    }

    private fun acquireParameterNames()
    {
        val m = URL_PARAM_PATTERN.matcher(urlPattern)
        while (m.find())
            parameterNames.add(m.group(1))
    }


    private fun extractParameters(matcher: Matcher): Map<String, String>
    {
        val values: MutableMap<String, String> = HashMap()
        for (i in 0 until matcher.groupCount())
        {
            val value = matcher.group(i + 1)
            if (value != null)
                values[parameterNames[i]] = value
        }

        return values
    }

    companion object
    {
        internal const val URL_PARAM_REGEX = "\\{(\\w*?)\\}"
        internal const val URL_PARAM_MATCH_REGEX = "\\([%\\\\w-.\\\\~!\\$&'\\\\(\\\\)\\\\*\\\\+,;=:\\\\[\\\\]@]+?\\)"
        internal val URL_PARAM_PATTERN: Pattern = Pattern.compile(URL_PARAM_REGEX)
        internal const val URL_FORMAT_REGEX = "(?:\\.\\{format\\})$"
        internal const val URL_FORMAT_MATCH_REGEX = "(?:\\\\.\\([\\\\w%]+?\\))?"
        internal const val URL_QUERY_STRING_REGEX = "(?:\\?.*?)?$"
    }
}