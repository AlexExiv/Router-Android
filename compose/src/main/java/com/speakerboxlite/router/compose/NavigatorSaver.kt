package com.speakerboxlite.router.compose

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.staticCompositionLocalOf
import java.io.Serializable

val LocalNavigatorSaver: ProvidableCompositionLocal<NavigatorSaver<*>> = staticCompositionLocalOf { defaultNavigatorSaver() }

fun interface NavigatorSaver<Saveable : Any>
{
    fun saver(key: String, stateHolder: SaveableStateHolder, viewModelProvider: RouterViewModelStoreProvider?): Saver<ComposeNavigator, Saveable>
}

fun defaultNavigatorSaver(): NavigatorSaver<Any> =
    NavigatorSaver { key, stateHolder, viewModelProvider ->
        listSaver(
            save = { navigator ->
                navigator.getStackEntriesSaveable()
           },
            restore = { items ->
                ComposeNavigator(key, stateHolder, viewModelProvider, items.map { StackEntry(it, viewModelProvider) })
            })
    }

data class StackEntrySaveable(val view: ViewCompose,
                              val animationController: AnimationControllerCompose?,
                              val isRemoving: Boolean,
                              val savedState: Bundle): Serializable, Parcelable
{
    constructor(entry: StackEntry): this(
        entry.view,
        entry.animationController,
        entry.isRemoving,
        Bundle())
    {
        entry.onSaveState(savedState)
    }

    constructor(inParcel: Parcel): this(
        inParcel.readSerializable() as ViewCompose,
        inParcel.readSerializable() as? AnimationControllerCompose,
        inParcel.readInt() != 0,
        inParcel.readBundle(StackEntrySaveable::class.java.classLoader)!!)

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int)
    {
        dest.writeSerializable(view)
        dest.writeSerializable(animationController)
        dest.writeInt(if (isRemoving) 1 else 0)
        dest.writeBundle(savedState)
    }

    companion object
    {
        @JvmField
        val CREATOR: Parcelable.Creator<StackEntrySaveable> = object : Parcelable.Creator<StackEntrySaveable>
        {
            override fun createFromParcel(inParcel: Parcel): StackEntrySaveable = StackEntrySaveable(inParcel)
            override fun newArray(size: Int): Array<StackEntrySaveable?> = arrayOfNulls(size)
        }
    }
}
