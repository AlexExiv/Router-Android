package com.speakerboxlite.router.ext

import android.app.Activity
import android.content.Intent
import com.speakerboxlite.router.annotations.InternalApi

@InternalApi
fun Activity.restartApp()
{
    finish()
    val intent = baseContext!!.packageManager!!.getLaunchIntentForPackage(baseContext!!.packageName!!)!!
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(intent)
}
