package com.speakerboxlite.router.samplemixed.tabs

import com.speakerboxlite.router.samplemixed.R
import com.speakerboxlite.router.samplemixed.base.fragment.BaseViewModelTabFragment
import com.speakerboxlite.router.samplemixed.databinding.FragmentTabsBinding

class TabsFragment: BaseViewModelTabFragment<TabsViewModel, FragmentTabsBinding>(R.layout.fragment_tabs)
{
    override fun onBindData()
    {
        super.onBindData()

        dataBinding.viewmodel = viewModel
        dataBinding.tabs.adapter = TabsAdapter(routerTabs, childFragmentManager, lifecycle)
        dataBinding.tabs.offscreenPageLimit = 1

        dataBinding.bottomNavigationView.setOnItemSelectedListener {
            routerTabs.route(TABS_MAP[it.itemId]!!)
        }

        dataBinding.bottomNavigationView.setOnItemReselectedListener {  }

        routerTabs.tabChangeCallback = {
            dataBinding.bottomNavigationView.selectedItemId = TABS_BACK_MAP[it]!!
            dataBinding.tabs.setCurrentItem(it, false)
        }
    }

    companion object
    {
        val TABS_MAP = mutableMapOf(
            R.id.tab0 to 0,
            R.id.tab1 to 1,
            R.id.tab2 to 2)

        val TABS_BACK_MAP = mutableMapOf(
            0 to R.id.tab0,
            1 to R.id.tab1,
            2 to R.id.tab2)
    }
}