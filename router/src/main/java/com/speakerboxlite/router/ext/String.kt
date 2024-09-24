package com.speakerboxlite.router.ext

import android.os.Bundle
import android.os.Parcel
import java.nio.charset.Charset
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun String.toBundle(): Bundle
{
    val bytes = Base64.decode(this)
    val p = Parcel.obtain()
    p.unmarshall(bytes, 0, bytes.size)
    p.setDataPosition(0)
    val bundle = Bundle()
    bundle.readFromParcel(p)
    p.recycle()

    return bundle
}