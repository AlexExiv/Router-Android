package com.speakerboxlite.router

interface RouterModelProvider

interface RouterModelStorage
{
    fun get(viewKey: String): ViewModel?
}
