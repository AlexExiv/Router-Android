package com.speakerboxlite.router.fragmentcompose

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.compose.ComposeHostViewRoot
import com.speakerboxlite.router.compose.LocalRouterManager
import com.speakerboxlite.router.fragment.HostClosableFragment
import com.speakerboxlite.router.fragment.IHostClosableFragment
import com.speakerboxlite.router.fragment.ext._viewKey

abstract class BaseHostComposeFragment: Fragment(),
    ComposeHostView,
    IHostClosableFragment by HostClosableFragment()
{
    override var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    override var root: ComposeHostViewRoot = mutableStateOf(null)

    protected var savedState = mapOf<String, List<Any?>>()

    override fun onStart()
    {
        super.onStart()
        root.value = { Navigator() }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)

        outState.putBundle(SAVED_STATE_KEY, savedState.toBundle())
    }

    @Composable
    fun Root()
    {
        val parentRegistry = LocalSaveableStateRegistry.current
        val registry = remember { SaveableStateRegistry(savedState) { parentRegistry?.canBeSaved(it) ?: true } }

        CompositionLocalProvider(
            LocalSaveableStateRegistry provides registry,
            LocalRouterManager provides routerManager)
        {
            root.value?.invoke()

            DisposableEffect(key1 = root.value)
            {
                onDispose {
                    savedState = registry.performSave()
                }
            }
        }
    }

    @Composable
    abstract fun Navigator()

    companion object
    {
        val SAVED_STATE_KEY = "com.speakerboxlite.router.fragmentcompose.BaseHostComposeFragment.Bundle"
    }
}

fun Bundle.toMap(): Map<String, List<Any?>>
{
    val map = mutableMapOf<String, List<Any?>>()
    keySet().forEach { key ->
        val list = getParcelableArrayList<Parcelable?>(key) as ArrayList<Any?>
        map[key] = list
    }

    return map
}

fun Map<String, List<Any?>>.toBundle(): Bundle
{
    val bundle = Bundle()
    forEach { (key, list) ->
        val arrayList = if (list is ArrayList<Any?>) list else ArrayList(list)
        bundle.putParcelableArrayList(key, arrayList as ArrayList<Parcelable?>)
    }
    return bundle
}
