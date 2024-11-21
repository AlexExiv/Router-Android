package com.speakerboxlite.router

import com.speakerboxlite.router.pattern.UrlPattern
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest
{
    @Test
    fun pattern()
    {
        val urlPattern = UrlPattern("/products/{id}/")
        val b0 = urlPattern.matches("/products/325?i=0&tt=123")
        val b1 = urlPattern.matches("/products/test")
        val b2 = urlPattern.matches("/products/test/rr")
        val b3 = urlPattern.matches("/products")
        val match0 = urlPattern.match("/products/325?i=0&tt=123")
        val match01 = urlPattern.match("/products/325/?i=0&tt=123")
        val match1 = urlPattern.match("/products/test")

        val urlPatternRoot = UrlPattern("/")
        val b01 = urlPatternRoot.matches("/products/325?i=0&tt=123")
        val b11 = urlPatternRoot.matches("/products/test")
        val b21 = urlPatternRoot.matches("/")

        println()
    }
}