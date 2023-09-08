package com.speakerboxlite.router.sample.tabs

import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.HostViewFactory
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.base.HostFragment
import com.speakerboxlite.router.sample.databinding.FragmentTabsBinding

class HostViewFactoryDefault: HostViewFactory
{
    override fun create(): HostView = HostFragment()
}

class TabsFragment: BaseViewModelFragment<TabsViewModel, FragmentTabsBinding>(R.layout.fragment_tabs)
{
    val routerTabs: RouterTabs by lazy {
        val r = router.createRouterTabs(HostViewFactoryDefault())
        r.tabChangeCallback = { dataBinding.bottomNavigationView.selectedItemId = TABS_BACK_MAP[it]!! }
        r
    }

    override fun onBindData()
    {
        super.onBindData()

        dataBinding.viewmodel = viewModel
        dataBinding.tabs.adapter = TabsAdapter(routerTabs, parentFragmentManager, lifecycle)

        dataBinding.bottomNavigationView.setOnItemSelectedListener {
            val i = TABS_MAP[it.itemId]!!
            dataBinding.tabs.setCurrentItem(i, false)
            true
        }
    }

    companion object
    {
        val TAB_KEY = "TAB_KEY"

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