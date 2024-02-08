package com.speakerboxlite.router

interface RouterStack
{
    val top: Router

    fun push(viewKey: String, router: Router)

    fun pushReel(viewKey: String, routerTabs: RouterTabs)
    fun switchReel(viewKey: String, index: Int)
    fun remove(viewKey: String)

    fun pop(toKey: String?): Router?
}

class RouterStackImpl: RouterStack
{
    override val top: Router get() = stack.lastOrNull()?.top ?: error("No specified top router")

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
}

interface RouterStackEntry
{
    val viewKey: String
    val top: Router?

    fun push(viewKey: String, router: Router): Boolean
    fun pop(toKey: String?): Router?
    fun remove(key: String): Boolean
}

class RouterStackSingle(override val viewKey: String,
                        override val top: Router?): RouterStackEntry
{
    override fun push(viewKey: String, router: Router): Boolean = false

    override fun pop(toKey: String?): Router? = null

    override fun remove(key: String): Boolean = false
}

class RouterStackReel(override val viewKey: String,
                      val routerTabs: RouterTabs): RouterStackEntry
{
    override val top: Router? get() = if (currentIndex == -1) null else stacks[currentIndex]?.lastOrNull()?.top

    internal var currentIndex = 0
    private val stacks = mutableMapOf<Int, MutableList<RouterStackEntry>>()

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
}

