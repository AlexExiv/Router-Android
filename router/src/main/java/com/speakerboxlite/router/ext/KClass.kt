package com.speakerboxlite.router.ext

import com.speakerboxlite.router.controllers.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

fun <T: Any> KClass<T>.retrieveComponent(): KClass<*>?
{
    var _st = supertypes[0]
    var _sc = superclasses[0]
    while (true)
    {
        val ca = _st.getChildArgument(Component::class)
        if (ca != null)
            return ca

        if (_sc.supertypes.isEmpty())
            break

        _st = _sc.supertypes[0]
        _sc = _sc.superclasses[0]
    }

    return null
}