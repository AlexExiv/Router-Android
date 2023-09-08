package com.speakerboxlite.router

interface ResultManager
{
    fun bind(from: String, to: String, result: Result<Any>)
    fun unbind(from: String)
    fun send(from: String, result: Any)
}