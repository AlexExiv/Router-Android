package com.speakerboxlite.router.command

import android.os.Bundle
import com.speakerboxlite.router.OnTabChangeCallback
import com.speakerboxlite.router.RoutePath
import java.io.Serializable

sealed class Command: Serializable
{
    object Close: Command()
    class CloseTo(val viewKey: String): Command()
    object CloseAll: Command()
    class StartModal(val viewKey: String, val params: Serializable?): Command()
    class Push(val path: RoutePath, val viewKey: String): Command()
    class Replace(val path: RoutePath, val byViewKey: String): Command()
    class BottomSheet(val viewKey: String): Command()
    class CloseBottomSheet(val viewKey: String): Command()
    class Dialog(val viewKey: String): Command()
    class CloseDialog(val viewKey: String): Command()
    class SubFragment(val containerId: Int, val viewKey: String): Command()
    class ChangeTab(val tabChangeCallback: OnTabChangeCallback, val tab: Int): Command()
}

fun Command.getViewKey(): String? = when (this)
    {
        is Command.StartModal -> this.viewKey
        is Command.Push -> this.viewKey
        is Command.Replace -> this.byViewKey
        is Command.BottomSheet -> this.viewKey
        is Command.Dialog -> this.viewKey
        is Command.SubFragment -> this.viewKey
        else -> null
    }

interface CommandBuffer
{
    fun bind(executor: CommandExecutor)
    fun unbind()

    fun apply(command: Command)
    fun sync(items: List<String>): List<String>

    fun performSave(bundle: Bundle)
    fun performRestore(bundle: Bundle)
}