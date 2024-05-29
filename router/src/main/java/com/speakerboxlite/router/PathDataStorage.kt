package com.speakerboxlite.router

import android.os.Bundle

interface PathDataStorage
{
    operator fun set(key: String, value: RoutePath?)
    operator fun get(key: String): RoutePath?

    fun performSave(bundle: Bundle)
    fun performRestore(bundle: Bundle)
}