package com.speakerboxlite.router.command

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.speakerboxlite.router.ext.checkMainThread

internal class CommandBufferImpl : CommandBuffer
{
    private var executor: CommandExecutor? = null
    private val buffer = mutableListOf<Command>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var executingCommands = false

    override fun bind(executor: CommandExecutor)
    {
        if (this.executor != null)
            unbind()

        this.executor = executor

        executor.onBind()
        executeCommands()
    }

    override fun unbind()
    {
        executor?.onUnbind()
        executor = null
    }

    override fun apply(command: Command)
    {
        checkMainThread("Applying of the commands only possible on the main thread.")

        if (executor == null)
            buffer.add(command)
        else
            tryExecuteCommand(command)
    }

    override fun sync(items: List<String>): List<String>
    {
        val itemsRet = executor?.sync(items)?.toMutableList() ?: error("Try to sync executor that hasn't been set")

        for (b in buffer)
        {
            val key = when (b)
            {
                is Command.StartModal -> b.key
                is Command.Push -> b.view.viewKey
                is Command.Replace -> b.byView.viewKey
                is Command.BottomSheet -> b.view.viewKey
                is Command.Dialog -> b.view.viewKey
                is Command.SubFragment -> b.view.viewKey
                else -> null
            }

            if (key != null)
                itemsRet.remove(key)
        }

        return itemsRet
    }

    override fun performSave(bundle: Bundle)
    {
        val root = Bundle()

        //root.putSerializable("1", buffer[0])

        bundle.putBundle(ROOT, root)
    }

    override fun performRestore(bundle: Bundle)
    {

    }

    private fun executeCommands()
    {
        if (executor == null)
            return

        executingCommands = false
        var removeCount = 0
        for (c in buffer)
        {
            try
            {
                executor!!.execute(c)
                removeCount += 1
            }
            catch (e: IllegalStateException)
            {
                /*
                if command has been executed with IllegalStateException stop the loop and restart it from this position on the next loop
                It could be the exception from transaction manager due to already executing something
                 */
                tryEnqueueExecuteCommands()
                break
            }
        }

        while (removeCount > 0) // remove executed commands
        {
            buffer.removeFirst()
            removeCount -= 1
        }
    }

    private fun tryExecuteCommand(command: Command)
    {
        checkMainThread("Executing of the commands only possible on the main thread.")

        try
        {
            executor!!.execute(command)
        }
        catch (e: IllegalStateException)
        {
            /*
            if command has been executed with IllegalStateException try to execute it on the next loop
            It could be the exception from transaction manager due to already executing something
             */

            buffer.add(command)
            tryEnqueueExecuteCommands()
        }
    }

    private fun tryEnqueueExecuteCommands()
    {
        if (executingCommands)
            return

        executingCommands = true
        mainHandler.postDelayed({ executeCommands() }, 1)
    }

    companion object
    {
        const val ROOT = "com.speakerboxlite.router.command.CommandBufferImpl"
    }
}