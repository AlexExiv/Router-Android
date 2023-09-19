package com.speakerboxlite.router.ext

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

fun KType.getChildArgument(clazz: KClass<*>): KClass<*>?
{
    for (a in arguments)
    {
        val c = a.type!!.classifier as? KClass<*> ?: continue
        if (c.isSubclassOf(clazz))
            return c
    }

    return null
}