package com.speakerboxlite.router.compose

import androidx.annotation.IdRes
import com.speakerboxlite.router.HOST_ACTIVITY_INTENT_DATA_KEY
import com.speakerboxlite.router.HOST_ACTIVITY_KEY
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.IntentBuilder
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewBTS
import com.speakerboxlite.router.ViewDialog
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandExecutor
import java.io.Serializable

interface ComposeViewHoster
{
    fun start(params: Serializable?, builder: IntentBuilder)
}

open class CommandExecutorCompose(val navigator: ComposeNavigator,
                                  val hoster: ComposeViewHoster? = null,
                                  val hostCloseable: HostCloseable? = null): CommandExecutor
{
    override fun onBind()
    {

    }

    override fun onUnbind()
    {

    }

    override fun execute(command: Command)
    {
        when (command)
        {
            is Command.Close -> close()
            is Command.CloseTo -> closeTo(command.key)
            is Command.CloseAll -> closeAll()
            is Command.StartModal -> startActivity(command.key, command.params)
            is Command.Dialog -> showDialog(command.view)
            is Command.CloseDialog -> closeDialog(command.key)
            is Command.Push -> push(command.path, command.view, command.animation as? AnimationControllerCompose)
            is Command.Replace -> replace(command.path, command.byView)
            is Command.BottomSheet -> showBottomSheet(command.view)
            is Command.CloseBottomSheet -> closeBottomSheet(command.key)
            is Command.SubFragment -> showSubFragment(command.containerId, command.view)
            is Command.ChangeTab -> command.tabChangeCallback(command.tab)
        }
    }

    override fun sync(items: List<String>): List<String>
    {
        val remove = mutableListOf<String>()
        for (i in items)
        {
            if (navigator.items.indexOfFirst { it.id == i } == -1)
                remove.add(i)
        }

        return remove
    }

    private fun close()
    {
        if (navigator.size > 1)
            navigator.pop()
        else
            closeAll()
    }

    private fun closeAll()
    {
        hostCloseable?.onCloseHost()
    }

    private fun closeTo(key: String)
    {
        if (navigator.size > 1)
        {
            navigator.popUntil { it.id == key }
        }
    }

    private fun startActivity(key: String, params: Serializable?)
    {
        hoster?.start(params) {
            intent ->
            intent.putExtra(HOST_ACTIVITY_KEY, key)
            params?.also { intent.putExtra(HOST_ACTIVITY_INTENT_DATA_KEY, it) }
        }
    }

    protected open fun push(path: RoutePath?, view: View, animationController: AnimationControllerCompose?)
    {
        val cv = view as? ViewCompose ?: error("")
        navigator.push(cv, animationController)
    }

    protected open fun replace(path: RoutePath?, view: View)
    {
        val cv = view as? ViewCompose ?: error("")
        navigator.replace(cv)
    }

    protected open fun showBottomSheet(view: View)
    {
        view as? ViewBTS ?: error("")
        val cv = view as? ViewCompose ?: error("")
        navigator.push(cv, null)
    }

    protected open fun closeBottomSheet(key: String)
    {
        close()
    }

    protected open fun showDialog(view: View)
    {
        view as? ViewDialog ?: error("")
        val cv = view as? ViewCompose ?: error("")
        navigator.push(cv, null)
    }

    protected open fun closeDialog(key: String)
    {
        close()
    }

    private fun showSubFragment(@IdRes containerId: Int, view: View)
    {

    }
}