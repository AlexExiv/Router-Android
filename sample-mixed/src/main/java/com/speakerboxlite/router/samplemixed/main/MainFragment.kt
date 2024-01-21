package com.speakerboxlite.router.samplemixed.main

import com.speakerboxlite.router.samplemixed.R
import com.speakerboxlite.router.samplemixed.base.fragment.BaseViewModelFragment
import com.speakerboxlite.router.samplemixed.databinding.FragmentMainBinding

class MainFragment: BaseViewModelFragment<MainViewModel, FragmentMainBinding>(R.layout.fragment_main)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}