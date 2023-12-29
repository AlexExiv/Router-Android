package com.speakerboxlite.router.compose

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.speakerboxlite.router.RouterViewModelStoreProvider

class SnapshotStateStack(items: List<StackEntry> = listOf(),
                         val viewModelProvider: RouterViewModelStoreProvider?,
                         minSize: Int = 0): Stack<StackEntry>
{

    @PublishedApi
    internal val stateStack: SnapshotStateList<StackEntry> = mutableStateListOf()

    init
    {
        stateStack.addAll(items)
    }

    override val items: List<StackEntry> by derivedStateOf { stateStack.toList() }

    override val lastItemOrNull: StackEntry? by derivedStateOf { stateStack.lastOrNull() }

    override val size: Int by derivedStateOf { stateStack.size }

    override val isEmpty: Boolean by derivedStateOf { stateStack.isEmpty() }

    override val canPop: Boolean by derivedStateOf { stateStack.size > minSize }

    override fun push(view: ViewCompose)
    {
        stateStack += StackEntry(view, viewModelProvider)
    }

    override fun replace(view: ViewCompose)
    {
        if (stateStack.isEmpty())
            push(view)
        else
            stateStack[stateStack.lastIndex] = StackEntry(view, viewModelProvider)
    }

    override fun pop()
    {
        if (canPop)
            removeLast()
    }

    override fun popAll()
    {
        popUntil { false }
    }

    override fun popToRoot()
    {
        while (stateStack.size > 1)
            removeLast()
    }

    override fun popUntil(predicate: (StackEntry) -> Boolean)
    {
        while (canPop && stateStack.isNotEmpty())
        {
            if (predicate(stateStack.last()))
                break

            removeLast()
        }
    }

    override fun clearEvent()
    {
    }

    protected open fun removeLast()
    {

    }
}