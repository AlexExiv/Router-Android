package com.speakerboxlite.router.sample.subtabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.sample.base.HostFragment
import com.speakerboxlite.router.sample.tabs.tab.TabPath0
import com.speakerboxlite.router.sample.tabs.tab.TabPath1

class SubTabsAdapter(val router: RouterTabs, fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle)
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
                else -> TODO()
            }

            hosterViews.add(hv)
        }
    }

    override fun createFragment(position: Int): Fragment =
        HostFragment().also { it.viewKey = hosterViews[position] }

    override fun getItemCount(): Int = 2
}