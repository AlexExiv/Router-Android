package com.speakerboxlite.router

interface ComponentProvider
{
    val appComponent: Any

    fun find(key: String): Any?
    fun bind(key: String, component: Any)
    fun unbind(key: String): Any?

    fun componentKey(key: String): String
    fun connectComponent(parentKey: String, childKey: String)
}