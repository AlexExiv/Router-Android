package com.speakerboxlite.router.sample.chain.sub

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentSubChainBinding

class SubChainFragment: BaseViewModelFragment<SubChainViewModel, FragmentSubChainBinding>(R.layout.fragment_sub_chain)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}