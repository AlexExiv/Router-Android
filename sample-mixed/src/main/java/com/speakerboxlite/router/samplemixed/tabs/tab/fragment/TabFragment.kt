package com.speakerboxlite.router.samplemixed.tabs.tab.fragment

import com.speakerboxlite.router.samplemixed.R
import com.speakerboxlite.router.samplemixed.base.fragment.BaseViewModelFragment
import com.speakerboxlite.router.samplemixed.databinding.FragmentTabBinding
import com.speakerboxlite.router.samplemixed.tabs.tab.TabViewModel

class TabFragment: BaseViewModelFragment<TabViewModel, FragmentTabBinding>(R.layout.fragment_tab)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}