package com.speakerboxlite.router.command

internal class CommandBufferImpl : CommandBuffer
{
    private var executor: CommandExecutor? = null
    private val buffer = mutableListOf<Command>()

    override fun bind(executor: CommandExecutor)
    {
        if (this.executor != null)
            unbind()

        this.executor = executor
        executor.onBind()

        buffer.forEach { executor.execute(it) }
        buffer.clear()
    }

    override fun unbind()
    {
        executor?.onUnbind()
        executor = null
    }

    override fun apply(command: Command)
    {
        if (executor == null)
            buffer.add(command)
        else
            executor!!.execute(command)
    }
}