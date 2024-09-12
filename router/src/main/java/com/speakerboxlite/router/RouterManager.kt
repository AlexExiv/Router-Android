package com.speakerboxlite.router

import android.os.Bundle

interface RouterManager
{
    val isAppRestarting: Boolean

    val top: Router?

    fun push(router: Router)
    fun getByKey(key: String): Router?

    operator fun set(key: String, value: Router?)
    operator fun get(key: String): Router?

    fun performSave(bundle: Bundle)
    fun performRestore(bundle: Bundle?)

    fun push(viewKey: String, router: Router)

    fun pushReel(viewKey: String, routerTabs: RouterTabs)
    fun switchReel(viewKey: String, index: Int)
    fun remove(viewKey: String)

    fun buildPathToRoot(key: String): List<PathComponent>

    fun provideDataStorage(): PathDataStorage
}