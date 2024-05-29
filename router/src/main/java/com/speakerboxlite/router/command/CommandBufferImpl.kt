package com.speakerboxlite.router.command

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.speakerboxlite.router.View
import com.speakerboxlite.router.controllers.AnimationController
import com.speakerboxlite.router.ext.checkMainThread
import com.speakerboxlite.router.ext.getSerializables
import com.speakerboxlite.router.ext.putSerializables

interface ViewFactoryInterface
{
    fun createView(key: String): View?
    fun createAnimation(view: View): AnimationController?
}

internal class CommandBufferImpl(val factory: ViewFactoryInterface?): CommandBuffer
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

        executor.onBind(factory)
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
            val key = b.getViewKey()
            if (key != null)
                itemsRet.remove(key)
        }

        return itemsRet
    }

    override fun performSave(bundle: Bundle)
    {
        val root = Bundle()

        root.putSerializables(BUFFER, buffer)

        bundle.putBundle(ROOT, root)
    }

    override fun performRestore(bundle: Bundle)
    {
        val root = bundle.getBundle(ROOT)
        buffer.clear()
        buffer.addAll(root!!.getSerializables(BUFFER) as? List<Command> ?: listOf())
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
        const val BUFFER = "com.speakerboxlite.router.command.CommandBufferImpl.buffer"
    }
}