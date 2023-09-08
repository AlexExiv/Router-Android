package com.speakerboxlite.router.sample.main

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentMainBinding

class MainFragment: BaseViewModelFragment<MainViewModel, FragmentMainBinding>(R.layout.fragment_main)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}