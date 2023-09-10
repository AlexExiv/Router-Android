package com.speakerboxlite.router

import com.speakerboxlite.router.command.CommandExecutor

typealias OnTabChangeCallback = (Int) -> Unit

interface RouterTabs
{
    var tabChangeCallback: OnTabChangeCallback?

    fun route(index: Int, path: RoutePath): HostView

    fun bindExecutor(executor: CommandExecutor)
    fun unbindExecutor()
}