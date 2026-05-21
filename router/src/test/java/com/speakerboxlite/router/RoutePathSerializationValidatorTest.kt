package com.speakerboxlite.router

import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.Serializable
import java.util.AbstractList

class RoutePathSerializationValidatorTest
{
    @After
    fun tearDown()
    {
        RouterConfigGlobal.validateRoutePathSerializable = true
    }

    @Test
    fun acceptsPathWithSerializableListImplementation()
    {
        RoutePathSerializationValidator.validate(CollectionPath(arrayListOf(Color())))
    }

    @Test
    fun rejectsPathWithNonSerializableListImplementation()
    {
        assertThrows(IllegalArgumentException::class.java) {
            RoutePathSerializationValidator.validate(CollectionPath(NonSerializableList(Color())))
        }
    }

    @Test
    fun canDisableRuntimeValidation()
    {
        RouterConfigGlobal.validateRoutePathSerializable = false

        RoutePathSerializationValidator.validate(CollectionPath(NonSerializableList(Color())))
    }
}

private data class CollectionPath(val colors: List<Color>): RoutePath

private data class Color(val r: Float = 0f): Serializable

private class NonSerializableList<T>(private val value: T): AbstractList<T>()
{
    override val size: Int = 1

    override fun get(index: Int): T = value
}