package com.speakerboxlite.router.sample.shared

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentSharedBinding

class SharedFragment: BaseViewModelFragment<SharedViewModel, FragmentSharedBinding>(R.layout.fragment_shared)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}