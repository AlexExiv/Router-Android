package com.speakerboxlite.router.command

interface CommandExecutor
{
    fun execute(command: Command)
}