package com.speakerboxlite.router.ext

import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

fun <T: Any> KClass<T>.retrieveComponent(): KClass<*>
{
    var _st = supertypes[0]
    var _sc = superclasses[0]
    while (true)
    {
        if (_st.arguments.size == 4)
            break

        _st = _sc.supertypes[0]
        _sc = _sc.superclasses[0]
    }

    return _st.arguments[3].type!!.classifier as KClass<*>
}