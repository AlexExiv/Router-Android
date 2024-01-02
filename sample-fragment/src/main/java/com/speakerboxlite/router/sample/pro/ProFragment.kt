package com.speakerboxlite.router.sample.pro

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentProBinding

class ProFragment: BaseViewModelFragment<ProViewModel, FragmentProBinding>(R.layout.fragment_pro)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}