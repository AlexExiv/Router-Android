package com.speakerboxlite.router.compose

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
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

    override val items: List<StackEntry> by derivedStateOf {
        stateStack.toList()
    }

    override val lastItemOrNull: StackEntry? by derivedStateOf {
        stateStack.lastOrNull()
    }

    override val size: Int by derivedStateOf {
        stateStack.size
    }

    override val isEmpty: Boolean by derivedStateOf {
        stateStack.isEmpty()
    }

    override val canPop: Boolean by derivedStateOf {
        stateStack.size > minSize
    }

    override fun push(view: ViewCompose) {
        stateStack += StackEntry(view, viewModelProvider)
    }

    override fun replace(view: ViewCompose) {
        if (stateStack.isEmpty()) {
            push(view)
        } else {
            stateStack[stateStack.lastIndex] = StackEntry(view, viewModelProvider)
        }
    }

    override fun pop(): Boolean =
        if (canPop) {
            stateStack.removeLast()
            true
        } else {
            false
        }

    override fun popAll() {
        popUntil { false }
    }

    override fun popUntil(predicate: (StackEntry) -> Boolean): Boolean {
        var success = false
        val shouldPop = {
            lastItemOrNull
                ?.let(predicate)
                ?.also { success = it }
                ?.not()
                ?: false
        }

        while (canPop && shouldPop()) {
            stateStack.removeLast()
        }

        return success
    }

    override fun clearEvent() {
    }
}