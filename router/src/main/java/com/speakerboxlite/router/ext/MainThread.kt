package com.speakerboxlite.router.ext

import android.os.Looper

fun checkMainThread(message: String? = null)
{
    check(Looper.getMainLooper().thread == Thread.currentThread()) { message ?: "This method should be called only on the main thread" }
}