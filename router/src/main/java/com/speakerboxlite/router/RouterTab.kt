package com.speakerboxlite.router

interface RouterTab: Router
{
    /**
     *  The index of the tab
     */
    val index: Int

    /**
     *
     */
    val routerTabs: RouterTabs

    /**
     * Closes all screens inside this tab up to the top screen.
     */
    fun closeTabToTop(): RouterTab?
}