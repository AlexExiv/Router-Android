package com.speakerboxlite.router.compose

interface IStackEntry
{
    fun onDispose()
}

interface Stack<Item: IStackEntry>
{
    val items: List<Item>
    val lastItemOrNull: Item?
    val size: Int

    val isEmpty: Boolean

    val canPop: Boolean

    fun push(view: ViewCompose)
    fun replace(view: ViewCompose)

    fun pop()
    fun popAll()
    fun popToRoot()
    fun popUntil(predicate: (Item) -> Boolean)

    fun clearEvent()
}