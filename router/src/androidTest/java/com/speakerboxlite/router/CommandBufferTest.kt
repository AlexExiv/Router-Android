package com.speakerboxlite.router

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandBufferImpl
import org.junit.Test
import org.junit.runner.RunWith

data class SimplePath(val i: Int): RoutePath

@RunWith(AndroidJUnit4::class)
class CommandBufferTest
{
    @Test
    fun testSave()
    {
        val command = CommandBufferImpl(null)
        command.apply(Command.Close)
        command.apply(Command.CloseAll)
        command.apply(Command.Push(SimplePath(1), "key"))
        command.apply(Command.ChangeTab({}, 1))

        val bundle = Bundle()
        command.performSave(bundle)

        val commandRestored = CommandBufferImpl(null)
        commandRestored.performRestore(bundle)

        println()
    }
}