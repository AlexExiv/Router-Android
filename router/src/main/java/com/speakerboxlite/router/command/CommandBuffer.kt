package com.speakerboxlite.router.command

import com.speakerboxlite.router.OnTabChangeCallback
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.controllers.AnimationController
import java.io.Serializable

sealed class Command
{
    object Close: Command()
    class CloseTo(val key: String): Command()
    object CloseAll: Command()
    class StartModal(val key: String, val params: Serializable?): Command()
    class ChangeHost(val key: String): Command()
    class Push(val path: RoutePath, val view: View, val animation: AnimationController?): Command()
    class Replace(val path: RoutePath, val byView: View, val animation: AnimationController?): Command()
    class BottomSheet(val view: View): Command()
    class CloseBottomSheet(val key: String): Command()
    class Dialog(val view: View): Command()
    class CloseDialog(val key: String): Command()
    class SubFragment(val containerId: Int, val view: View): Command()
    class ChangeTab(val tabChangeCallback: OnTabChangeCallback, val tab: Int): Command()
}

interface CommandBuffer
{
    fun bind(executor: CommandExecutor)
    fun unbind()

    fun apply(command: Command)
}