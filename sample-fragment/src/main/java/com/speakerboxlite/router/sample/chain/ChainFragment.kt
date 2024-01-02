package com.speakerboxlite.router.sample.chain

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentChainBinding

class ChainFragment: BaseViewModelFragment<ChainViewModel, FragmentChainBinding>(R.layout.fragment_chain)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}