package com.speakerboxlite.router.sample.main

import androidx.fragment.app.viewModels
import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentMainBinding

class MainFragment: BaseViewModelFragment<MainViewModel, FragmentMainBinding>(R.layout.fragment_main)
{
    val viewModel1: MainViewModel by viewModels<MainViewModel>()
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}