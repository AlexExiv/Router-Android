package com.speakerboxlite.router

import android.app.Activity
import android.content.Intent

const val HOST_ACTIVITY_KEY = "HOST_ACTIVITY_KEY"
const val START_ACTIVITY_KEY = "START_ACTIVITY"

val Activity.hostActivityKey: String get() = intent.extras?.getString(HOST_ACTIVITY_KEY) ?: START_ACTIVITY_KEY

interface HostActivityFactory
{
    fun create(): Intent
}