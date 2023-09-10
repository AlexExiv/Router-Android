package com.speakerboxlite.router.sample.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.sample.tabs.tab.TabPath
import com.speakerboxlite.router.sample.tabs.tab.TabSingletonPath

class TabsAdapter(val router: RouterTabs, fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle)
{
    val hosterViews = mutableListOf<HostView>()

    init
    {
        for (i in 0 until itemCount)
        {
            val hv = when (i)
            {
                1 -> router.route(i, TabSingletonPath(i))
                else -> router.route(i, TabPath(i))
            }

            hosterViews.add(hv)
        }
    }

    override fun createFragment(position: Int): Fragment = hosterViews[position] as Fragment

    override fun getItemCount(): Int = 3
}