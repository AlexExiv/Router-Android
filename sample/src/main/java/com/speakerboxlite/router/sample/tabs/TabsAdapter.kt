package com.speakerboxlite.router.sample.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.sample.tabs.tab.TabPath

class TabsAdapter(val router: RouterTabs, fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle)
{
    override fun createFragment(position: Int): Fragment = router.route(position, TabPath(position)) as Fragment

    override fun getItemCount(): Int = 3
}