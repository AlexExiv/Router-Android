package com.speakerboxlite.router.sample.subtabs

import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.fragment.CommandExecutorFragment
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.samplefragment.databinding.FragmentSubTabsBinding

class SubTabsFragment: BaseViewModelFragment<SubTabsViewModel, FragmentSubTabsBinding>(R.layout.fragment_sub_tabs)
{
    lateinit var localRouterTabs: RouterTabs

    override fun onStart()
    {
        super.onStart()
        localRouterTabs.bindExecutor(CommandExecutorFragment(requireActivity(), 0, childFragmentManager))
    }

    override fun onStop()
    {
        super.onStop()
        localRouterTabs.unbindExecutor()
    }

    override fun onBindData()
    {
        super.onBindData()

        localRouterTabs = localRouter.createRouterTabs(viewKey)
        dataBinding.viewmodel = viewModel
        dataBinding.tabs.adapter = SubTabsAdapter(localRouterTabs, childFragmentManager, lifecycle)

        viewModel.selectedTab.observe(this) { localRouterTabs.route(it) }

        localRouterTabs.tabChangeCallback = {
            viewModel.selectedTab.postValue(it)
            dataBinding.tabs.setCurrentItem(it, false)
        }
    }
}