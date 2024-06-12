package com.speakerboxlite.router

import android.os.Bundle
import com.speakerboxlite.router.ext.getBundles
import com.speakerboxlite.router.ext.putBundles

data class PathComponent(
    val viewKey: String,
    val router: Router)

interface RouterStack
{
    val top: Router?

    fun push(viewKey: String, router: Router)

    fun pushReel(viewKey: String, routerTabs: RouterTabs)
    fun switchReel(viewKey: String, index: Int)
    fun remove(viewKey: String)

    fun pop(toKey: String?): Router?

    fun buildPathToRoot(): List<PathComponent>

    fun performSaveStack(bundle: Bundle)
    fun performRestore(bundle: Bundle, routerManager: RouterManager)
}

class RouterStackImpl: RouterStack
{
    override val top: Router? get() = stack.lastOrNull()?.top

    private val stack = mutableListOf<RouterStackEntry>()

    override fun push(viewKey: String, router: Router)
    {
        if (router is RouterTab)
        {
            val reel = stack.firstOrNull { it is RouterStackReel && it.routerTabs == router.routerTabs } ?: error("Try to add RouterTab before its RouterTabs has pushed the Reel")
            reel.push(viewKey, router)
        }
        else
            stack.add(RouterStackSingle(viewKey, router))
    }

    override fun pushReel(viewKey: String, routerTabs: RouterTabs)
    {
        if (stack.count { it.viewKey == viewKey } == 2)
            return

        val index = stack.indexOfLast { it.viewKey == viewKey }
        check(index != -1) { "Try to add Reel before the Root view" }
        stack.add(index + 1, RouterStackReel(viewKey, routerTabs))
    }

    override fun switchReel(viewKey: String, index: Int)
    {
        val item = stack.lastOrNull { it.viewKey == viewKey }  ?: error("Reel with viewKey: $viewKey has not been found")
        val reel = item as? RouterStackReel ?: error("Item with viewKey: $viewKey is not a Reel")
        reel.currentIndex = index
    }

    override fun remove(viewKey: String)
    {
        for (i in stack.indices)
        {
            val s = stack[i]
            if (s.remove(viewKey))
                break

            if (s.viewKey == viewKey)
            {
                stack.removeAt(i)
                if ((i < stack.size) && (stack[i].viewKey == viewKey))
                    stack.removeAt(i)

                break
            }
        }
    }

    override fun pop(toKey: String?): Router?
    {
        val ret = stack.last().pop(toKey)
        if (ret != null)
            return ret

        val e = stack.removeLast()
        if (e is RouterStackReel)
            stack.removeLast()

        return stack.last().top
    }

    override fun buildPathToRoot(): List<PathComponent>
    {
        val path = mutableListOf<PathComponent>()
        for (i in (stack.size - 1) downTo 0)
            stack[i].addToPath(path)

        return path
    }

    override fun performSaveStack(bundle: Bundle)
    {
        bundle.putBundles(STACK, stack.map { it.toBundle() })
    }

    override fun performRestore(bundle: Bundle, routerManager: RouterManager)
    {
        stack.clear()
        bundle.getBundles(STACK)!!.forEach { stack.add(routerStackEntryFromBundle(it, routerManager)) }
    }

    companion object
    {
        val STACK = "com.speakerboxlite.router.RouterStackImpl.stack"
    }
}

private const val RouterStackSingleType = 0
private const val RouterStackReelType = 1

interface RouterStackEntry
{
    val viewKey: String
    val top: Router?

    fun push(viewKey: String, router: Router): Boolean
    fun pop(toKey: String?): Router?
    fun remove(key: String): Boolean

    fun addToPath(path: MutableList<PathComponent>)

    fun toBundle(): Bundle
}

private fun routerStackEntryFromBundle(bundle: Bundle, routerManager: RouterManager): RouterStackEntry =
    when (bundle.getInt("type"))
    {
        RouterStackSingleType -> RouterStackSingle.fromBundle(bundle, routerManager)
        RouterStackReelType -> RouterStackReel.fromBundle(bundle, routerManager)
        else -> error("")
    }

class RouterStackSingle(
    override val viewKey: String,
    override val top: Router?): RouterStackEntry
{
    override fun push(viewKey: String, router: Router): Boolean = false

    override fun pop(toKey: String?): Router? = null

    override fun remove(key: String): Boolean = false

    override fun addToPath(path: MutableList<PathComponent>)
    {
        path.add(PathComponent(viewKey, top!!))
    }

    override fun toBundle(): Bundle = Bundle()
        .also {
            it.putInt("type", RouterStackSingleType)
            it.putString("viewKey", viewKey)
        }

    companion object
    {
        fun fromBundle(bundle: Bundle, routerManager: RouterManager): RouterStackSingle =
            bundle
                .getString("viewKey")!!
                .let { RouterStackSingle(it, routerManager[it]) }
    }
}

class RouterStackReel(
    override val viewKey: String,
    val routerTabs: RouterTabs): RouterStackEntry
{
    override val top: Router? get() = if (currentIndex == -1) null else stacks[currentIndex]?.lastOrNull()?.top

    internal var currentIndex = 0
    private val stacks = mutableMapOf<Int, MutableList<RouterStackSingle>>()

    override fun push(viewKey: String, router: Router): Boolean
    {
        if (router is RouterTab)
        {
            if (stacks[router.index] == null)
                stacks[router.index] = mutableListOf()

            stacks[router.index]!!.add(RouterStackSingle(viewKey, router))

            return true
        }

        return false
    }

    override fun pop(toKey: String?): Router?
    {
        check(currentIndex != -1) { "Try to pop router but currentIndex has not been set" }

        if (stacks[currentIndex]!!.size > 1)
        {
            stacks[currentIndex]!!.removeLast()
            return stacks[currentIndex]!!.last().top
        }

        return null
    }

    override fun remove(key: String): Boolean
    {
        for (k in stacks.keys)
        {
            for (i in stacks[k]!!.indices)
            {
                if (stacks[k]!![i].viewKey == key)
                {
                    stacks[k]!!.removeAt(i)
                    return true
                }
            }
        }

        return false
    }

    override fun addToPath(path: MutableList<PathComponent>)
    {
        val tabStack = stacks[currentIndex]!!
        for (i in (tabStack.size - 1) downTo 0)
            path.add(PathComponent(tabStack[i].viewKey, tabStack[i].top!!))
    }

    override fun toBundle(): Bundle =
        Bundle()
            .also {
                it.putInt("type", RouterStackReelType)
                it.putString("viewKey", viewKey)
                it.putInt("currentIndex", currentIndex)
                val stacksBundle = Bundle()
                stacks.forEach { stacksBundle.putBundles(it.key.toString(), it.value.map { it.toBundle() }) }
                it.putBundle("stacks", stacksBundle)
            }

    companion object
    {
        fun fromBundle(bundle: Bundle, routerManager: RouterManager): RouterStackReel =
            bundle
                .getString("viewKey")!!
                .let {
                    val rss = RouterStackReel(it, routerManager[it]!!.createRouterTabs(it))
                    rss.currentIndex = bundle.getInt("currentIndex")
                    val stacksBundle = bundle.getBundle("stacks")!!
                    stacksBundle.keySet().forEach {
                        rss.stacks[it.toInt()] = stacksBundle.getBundles(it)!!.map { RouterStackSingle.fromBundle(it, routerManager) }.toMutableList()
                    }

                    return@let rss
                }
    }
}

