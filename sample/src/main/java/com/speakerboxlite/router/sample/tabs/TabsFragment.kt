package com.speakerboxlite.router.sample.tabs

import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.command.CommandExecutorAndroid
import com.speakerboxlite.router.sample.App
import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentTabsBinding

class TabsFragment: BaseViewModelFragment<TabsViewModel, FragmentTabsBinding>(R.layout.fragment_tabs)
{
    val routerTabs: RouterTabs by lazy { router.createRouterTabs(viewKey, false) }

    override fun onResume()
    {
        super.onResume()
        routerTabs.bindExecutor(CommandExecutorAndroid(requireActivity(), 0, childFragmentManager, requireActivity().application as App))
    }

    override fun onPause()
    {
        routerTabs.unbindExecutor()
        super.onPause()
    }

    override fun onBindData()
    {
        super.onBindData()

        dataBinding.viewmodel = viewModel
        dataBinding.tabs.adapter = TabsAdapter(routerTabs, childFragmentManager, lifecycle)

        dataBinding.bottomNavigationView.setOnItemSelectedListener {
            val i = TABS_MAP[it.itemId]!!
            dataBinding.tabs.setCurrentItem(i, true)
            true
        }

        routerTabs.tabChangeCallback = { dataBinding.bottomNavigationView.selectedItemId = TABS_BACK_MAP[it]!! }
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