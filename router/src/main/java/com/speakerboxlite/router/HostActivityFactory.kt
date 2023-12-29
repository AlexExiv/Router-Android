package com.speakerboxlite.router

import android.app.Activity
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.command.IntentBuilder
import java.io.Serializable

internal const val HOST_ACTIVITY_KEY = "com.speakerboxlite.router.HOST_ACTIVITY_KEY"
internal const val HOST_ACTIVITY_INTENT_DATA_KEY = "com.speakerboxlite.router.HOST_ACTIVITY_INTENT_DATA_KEY"

const val START_ACTIVITY_KEY = "START_ACTIVITY"

val Activity.hostActivityKey: String get() = intent.extras?.getString(HOST_ACTIVITY_KEY) ?: START_ACTIVITY_KEY
val Activity.hostActivityParams: Serializable? get() = intent.extras?.getSerializable(HOST_ACTIVITY_INTENT_DATA_KEY)

interface HostActivityFactory
{
    fun startActivity(params: Serializable?, builder: IntentBuilder)
}

interface HostFragmentComposeFactory
{
    fun createHostFragment(): Fragment
}