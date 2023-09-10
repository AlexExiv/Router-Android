package com.speakerboxlite.router

internal class CommandBufferImpl : CommandBuffer
{
    private var executor: CommandExecutor? = null
    private val buffer = mutableListOf<Command>()

    override fun bind(executor: CommandExecutor)
    {
        this.executor = executor
        buffer.forEach { executor.execute(it) }
        buffer.clear()
    }

    override fun unbind()
    {
        this.executor = null
    }

    override fun apply(command: Command)
    {
        if (executor == null)
            buffer.add(command)
        else
            executor!!.execute(command)
    }
}