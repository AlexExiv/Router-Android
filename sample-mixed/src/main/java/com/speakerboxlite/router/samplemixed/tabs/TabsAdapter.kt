package com.speakerboxlite.router.samplemixed.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.samplemixed.base.fragment.BaseHostFragment
import com.speakerboxlite.router.samplemixed.base.fragment.TabHostComposeFragment
import com.speakerboxlite.router.samplemixed.tabs.tab.TabPath0
import com.speakerboxlite.router.samplemixed.tabs.tab.TabPath1
import com.speakerboxlite.router.samplemixed.tabs.tab.TabPath2

class TabsAdapter(val router: RouterTabs, fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle)
{
    val hosterViews = mutableListOf<String>()

    init
    {
        for (i in 0 until itemCount)
        {
            val hv = when (i)
            {
                0 -> router.route(i, TabPath0(), false)
                1 -> router.route(i, TabPath1(), false)
                2 -> router.route(i, TabPath2(), false)
                else -> TODO()
            }

            hosterViews.add(hv)
        }
    }

    override fun createFragment(position: Int): Fragment
    {
        val hostFrag = when (position)
        {
            0 -> BaseHostFragment()
            else -> TabHostComposeFragment()
        }

        return hostFrag.also { it.viewKey = hosterViews[position] }
    }

    override fun getItemCount(): Int = 3
}