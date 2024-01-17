package com.speakerboxlite.router.fragmentcompose

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.ui.platform.ComposeView

abstract class TabHostComposeFragment: BaseHostComposeFragment()
{
    private var savedState = mapOf<String, List<Any?>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        withFragment(this)

        val bundle = savedInstanceState?.getBundle(SAVED_STATE_KEY)
        if (bundle != null)
            savedState = bundle.toMap()

        return ComposeView(requireContext()).also {
            it.setContent {
                val parentRegistry = LocalSaveableStateRegistry.current
                val registry = remember { SaveableStateRegistry(savedState) { parentRegistry?.canBeSaved(it) ?: true } }

                CompositionLocalProvider(LocalSaveableStateRegistry provides registry)
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
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)

        outState.putBundle(SAVED_STATE_KEY, savedState.toBundle())
    }

    companion object
    {
        val SAVED_STATE_KEY = "com.speakerboxlite.router.fragmentcompose.TabHostComposeFragment.Bundle"
    }
}

private fun Bundle.toMap(): Map<String, List<Any?>>
{
    val map = mutableMapOf<String, List<Any?>>()
    keySet().forEach { key ->
        val list = getParcelableArrayList<Parcelable?>(key) as ArrayList<Any?>
        map[key] = list
    }

    return map
}

private fun Map<String, List<Any?>>.toBundle(): Bundle
{
    val bundle = Bundle()
    forEach { (key, list) ->
        val arrayList = if (list is ArrayList<Any?>) list else ArrayList(list)
        bundle.putParcelableArrayList(key, arrayList as ArrayList<Parcelable?>)
    }
    return bundle
}
