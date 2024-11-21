package com.speakerboxlite.router.compose

import com.speakerboxlite.router.View
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.command.ViewFactoryInterface

open class CommandExecutorComposeLocal(val navigator: ComposeNavigator): CommandExecutor
{
    protected var viewFactory: ViewFactoryInterface? = null

    override fun onBind(factory: ViewFactoryInterface?)
    {
        viewFactory = factory
    }

    override fun onUnbind()
    {
        viewFactory = null
    }

    override fun execute(command: Command)
    {
        val viewKey = when (command)
        {
            is Command.SubFragment -> command.viewKey
            else -> error("")
        }

        checkNotNull(viewFactory) { "ViewFactory hasn't been set" }
        val view = viewFactory?.createView(viewKey) ?: return
        showView(view)
    }

    override fun sync(items: List<String>): List<String> =
        if (navigator.items.firstOrNull()?.id == items.firstOrNull())
            items.take(1)
        else
            listOf()

    protected open fun showView(view: View)
    {
        view as? ViewCompose ?: error("")
        navigator.replace(view)
    }
}