package com.speakerboxlite.router.compose

interface Stack<Item>
{
    val items: List<Item>
    val lastItemOrNull: Item?
    val size: Int

    val isEmpty: Boolean

    val canPop: Boolean

    fun push(view: ViewCompose)
    fun replace(view: ViewCompose)

    fun pop(): Boolean
    fun popAll()
    fun popUntil(predicate: (Item) -> Boolean): Boolean

    fun clearEvent()
}