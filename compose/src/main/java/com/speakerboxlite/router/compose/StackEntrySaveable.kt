package com.speakerboxlite.router.compose

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class StackEntrySaveable(
    val view: ViewCompose,
    val animationController: AnimationControllerCompose?,
    val isRemoving: Boolean,
    val subKeys: List<String>,
    val savedState: Bundle): Serializable, Parcelable
{
    constructor(entry: StackEntry): this(
        entry.view,
        entry.animationController,
        entry.isRemoving,
        entry.subKeys,
        Bundle())
    {
        entry.onSaveState(savedState)
    }

    constructor(inParcel: Parcel): this(
        inParcel.readSerializable() as ViewCompose,
        inParcel.readSerializable() as? AnimationControllerCompose,
        inParcel.readInt() != 0,
        mutableListOf<String>().apply { inParcel.readStringList(this) },
        inParcel.readBundle(StackEntrySaveable::class.java.classLoader)!!)

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int)
    {
        dest.writeSerializable(view)
        dest.writeSerializable(animationController)
        dest.writeInt(if (isRemoving) 1 else 0)
        dest.writeStringList(subKeys)
        dest.writeBundle(savedState)
    }

    companion object
    {
        @JvmField
        val CREATOR: Parcelable.Creator<StackEntrySaveable> = object :
            Parcelable.Creator<StackEntrySaveable>
        {
            override fun createFromParcel(inParcel: Parcel): StackEntrySaveable =
                StackEntrySaveable(inParcel)
            override fun newArray(size: Int): Array<StackEntrySaveable?> = arrayOfNulls(size)
        }
    }
}