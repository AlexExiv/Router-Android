package com.speakerboxlite.router.command

interface CommandExecutor
{
    fun onBind()
    fun onUnbind()

    fun execute(command: Command)
    fun sync(items: List<String>): List<String>
}