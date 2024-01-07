package com.speakerboxlite.router

interface RouterStack
{
    val top: Router

    fun push(viewKey: String, router: Router)

    fun pushReel(viewKey: String, routerTabs: RouterTabs)
    fun switchReel(viewKey: String, index: Int)

    fun pop(toKey: String?): Router?
}

class RouterStackImpl: RouterStack
{
    override val top: Router get() = stack.lastOrNull()?.top ?: error("No specified top router")

    private val stack = mutableListOf<RouterStackEntry>()

    override fun push(viewKey: String, router: Router)
    {
        if (router is RouterTab || router is RouterTabInjector)
        {
            val reel = stack.last() as? RouterStackReel ?: error("The last item is not a Reel")
            reel.push(router)
        }
        else
            stack.add(RouterStackSingle(viewKey, router))
    }

    override fun pushReel(viewKey: String, routerTabs: RouterTabs)
    {
        if (stack.count { it.viewKey == viewKey } == 2)
            return

        check(stack.last().viewKey == viewKey) { "Try to add Reel before the Root view" }
        stack.add(RouterStackReel(viewKey))
    }

    override fun switchReel(viewKey: String, index: Int)
    {
        val item = stack.lastOrNull { it.viewKey == viewKey }  ?: error("Reel with viewKey: $viewKey has not been found")
        val reel = item as? RouterStackReel ?: error("Item with viewKey: $viewKey is not a Reel")
        reel.currentIndex = index
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

    fun push(router: Router): Boolean
    fun pop(toKey: String?): Router?
}

class RouterStackSingle(override val viewKey: String,
                        override val top: Router?): RouterStackEntry
{
    override fun push(router: Router): Boolean = false

    override fun pop(toKey: String?): Router? = null
}

class RouterStackReel(override val viewKey: String): RouterStackEntry
{
    override val top: Router? get() = if (currentIndex == -1) null else stacks[currentIndex]?.lastOrNull()

    internal var currentIndex = 0
    private val stacks = mutableMapOf<Int, MutableList<Router>>()

    override fun push(router: Router): Boolean
    {
        if (router is RouterTab || router is RouterTabInjector)
        {
            val index = when (router)
            {
                is RouterTab -> router.index
                is RouterTabInjector -> router.index
                else -> 0
            }

            if (stacks[index] == null)
                stacks[index] = mutableListOf()

            stacks[index]!!.add(router)

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
            return stacks[currentIndex]!!.last()
        }

        return null
    }
}

