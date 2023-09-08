package com.speakerboxlite.router

sealed class Command
{
    object Close: Command()
    class CloseTo(val key: String): Command()
    class StartModal(val key: String): Command()
    class Push(val view: View<*>): Command()
    class BottomSheet(val view: View<*>): Command()
    class CloseBottomSheet(val key: String): Command()
    class Dialog(val view: View<*>): Command()
    class CloseDialog(val key: String): Command()
    class SubFragment(val containerId: Int, val view: View<*>): Command()
}

interface CommandBuffer
{
    fun bind(executor: CommandExecutor)
    fun unbind()

    fun apply(command: Command)
}