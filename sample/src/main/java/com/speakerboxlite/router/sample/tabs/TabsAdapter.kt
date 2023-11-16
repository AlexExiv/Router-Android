package com.speakerboxlite.router.sample.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.sample.base.HostFragment
import com.speakerboxlite.router.sample.tabs.tab.TabAuthPath
import com.speakerboxlite.router.sample.tabs.tab.TabPath
import com.speakerboxlite.router.sample.tabs.tab.TabSingletonPath

class TabsAdapter(val router: RouterTabs, fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle)
{
    val hosterViews = mutableListOf<String>()

    init
    {
        for (i in 0 until itemCount)
        {
            val hv = when (i)
            {
                1 -> router.route(i, TabSingletonPath(i), false)
                2 -> router.route(i, TabAuthPath(i), false)
                else -> router.route(i, TabPath(i), false)
            }

            hosterViews.add(hv)
        }
    }

    override fun createFragment(position: Int): Fragment =
        HostFragment().also { it.viewKey = hosterViews[position] }

    override fun getItemCount(): Int = 3
}