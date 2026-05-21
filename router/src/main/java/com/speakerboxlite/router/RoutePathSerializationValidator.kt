package com.speakerboxlite.router

import java.io.ByteArrayOutputStream
import java.io.NotSerializableException
import java.io.ObjectOutputStream

internal object RoutePathSerializationValidator
{
    fun validate(path: RoutePath)
    {
        if (!RouterConfigGlobal.validateRoutePathSerializable)
            return

        try
        {
            ObjectOutputStream(ByteArrayOutputStream()).use {
                it.writeObject(path)
            }
        }
        catch (e: NotSerializableException)
        {
            throw IllegalArgumentException(
                "RoutePath ${path::class.qualifiedName} must be fully Serializable at runtime. " +
                    "Non-serializable object: ${e.message}. " +
                    "If a property is declared as List, Set, or Map, pass a Serializable implementation such as ArrayList, LinkedHashSet, or LinkedHashMap.",
                e)
        }
    }
}
