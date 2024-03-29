package com.speakerboxlite.router.sample.step.replace

import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.samplefragment.databinding.FragmentReplaceBinding

class ReplaceFragment: BaseViewModelFragment<ReplaceViewModel, FragmentReplaceBinding>(R.layout.fragment_replace)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}