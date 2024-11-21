package com.speakerboxlite.router.ext

import android.os.Bundle
import android.os.Parcel
import java.io.Serializable
import java.nio.charset.Charset
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun Bundle.putBundles(key: String, bundles: List<Bundle>)
{
    val bundle = Bundle()
    bundles.forEachIndexed { i, b -> bundle.putBundle(i.toString(), b) }
    putBundle(key, bundle)
}

fun Bundle.getBundles(key: String): List<Bundle>?
{
    val bundle = getBundle(key) ?: return null
    val indices = bundle.keySet().map { it.toInt() }.sorted()
    val list = mutableListOf<Bundle>()
    indices.forEach { list.add(bundle.getBundle(it.toString())!!) }
    return list
}

fun Bundle.putSerializables(key: String, items: List<Serializable>)
{
    val bundle = Bundle()
    items.forEachIndexed { i, b -> bundle.putSerializable(i.toString(), b) }
    putBundle(key, bundle)
}

fun Bundle.getSerializables(key: String): List<Serializable>?
{
    val bundle = getBundle(key) ?: return null
    val indices = bundle.keySet().map { it.toInt() }.sorted()
    val list = mutableListOf<Serializable>()
    indices.forEach { list.add(bundle.getSerializable(it.toString())!!) }
    return list
}

@OptIn(ExperimentalEncodingApi::class)
fun Bundle.toStringUTF(): String
{
    val p = Parcel.obtain()
    writeToParcel(p, 0)
    val bytes = p.marshall()
    val str = Base64.encode(bytes)
    p.recycle()
    return str
}