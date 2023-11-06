package com.speakerboxlite.router.zombie

import com.speakerboxlite.router.OnTabChangeCallback
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.command.CommandExecutor
import java.util.UUID

internal class RouterTabsZombie : RouterTabs
{
    override var tabChangeCallback: OnTabChangeCallback? = null

    override fun route(index: Int, path: RoutePath, recreate: Boolean): String = UUID.randomUUID().toString()

    override fun bindExecutor(executor: CommandExecutor)
    {

    }

    override fun unbindExecutor()
    {

    }
}