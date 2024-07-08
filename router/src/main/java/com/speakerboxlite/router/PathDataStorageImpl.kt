package com.speakerboxlite.router

import android.os.Bundle

class PathDataStorageImpl : PathDataStorage
{
    private val data = mutableMapOf<String, RoutePath>()

    override fun set(key: String, value: RoutePath?)
    {
        if (value == null)
        {
            data.remove(key)
            RouterConfigGlobal.log(TAG, "Bound data after release: ${data.size}")
        }
        else
        {
            data[key] = value
            RouterConfigGlobal.log(TAG, "Bound data: ${data.size}")
        }
    }

    override fun get(key: String): RoutePath? = data[key]

    override fun performSave(bundle: Bundle)
    {
        val root = Bundle()
        val pathDataBundle = Bundle()
        data.forEach {
            pathDataBundle.putSerializable(it.key, it.value)
        }
        root.putBundle(DATA, pathDataBundle)

        bundle.putBundle(ROOT, root)
    }

    override fun performRestore(bundle: Bundle)
    {
        val root = bundle.getBundle(ROOT)!!
        val pathDataBundle = root.getBundle(DATA)!!
        data.clear()
        pathDataBundle.keySet().forEach { data[it] = pathDataBundle.getSerializable(it) as RoutePath }
    }

    companion object
    {
        val TAG = "PathDataStorageImpl"

        val ROOT = "com.speakerboxlite.router.PathDataStorageImpl"
        val DATA = "com.speakerboxlite.router.PathDataStorageImpl.data"
    }
}