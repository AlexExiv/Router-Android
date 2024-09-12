package com.speakerboxlite.router

import com.speakerboxlite.router.controllers.ComponentCleanable

class ComponentProviderImpl(override val appComponent: Any): ComponentProvider
{
    private val componentByKey = mutableMapOf<String, Any>()
    private val parentByChild = mutableMapOf<String, String>()

    override fun find(key: String): Any? = componentByKey[key]

    override fun bind(key: String, component: Any)
    {
        componentByKey[key] = component
    }

    override fun unbind(key: String): Any? =
        componentByKey.remove(key)?.also {
            (it as? ComponentCleanable)?.onCleared()
        }

    override fun componentKey(key: String): String
    {
        var lastKey = key
        while (true)
        {
            if (parentByChild[lastKey] == null)
                break

            lastKey = parentByChild[lastKey]!!
        }

        return lastKey
    }

    override fun connectComponent(parentKey: String, childKey: String)
    {
        parentByChild[childKey] = parentKey
    }
}