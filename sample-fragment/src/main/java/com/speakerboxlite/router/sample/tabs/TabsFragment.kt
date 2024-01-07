package com.speakerboxlite.router.sample.tabs

import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.sample.base.BaseViewModelTabFragment
import com.speakerboxlite.router.samplefragment.databinding.FragmentTabsBinding

class TabsFragment: BaseViewModelTabFragment<TabsViewModel, FragmentTabsBinding>(R.layout.fragment_tabs)
{
    override fun onBindData()
    {
        super.onBindData()

        dataBinding.viewmodel = viewModel
        dataBinding.tabs.adapter = TabsAdapter(routerTabs, childFragmentManager, lifecycle)

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