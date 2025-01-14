package com.speakerboxlite.router

import android.os.Bundle

interface ComponentProvider
{
    val appComponent: Any

    fun find(key: String): Any?
    fun bind(key: String, component: Any)
    fun unbind(key: String): Any?

    fun componentKey(key: String): String
    fun connectComponent(parentKey: String, childKey: String)

    fun performSave(bundle: Bundle)
    fun performRestore(bundle: Bundle)
}