package com.speakerboxlite.router.sample.step

import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.samplefragment.databinding.FragmentStepBinding

class StepFragment: BaseViewModelFragment<StepViewModel, FragmentStepBinding>(R.layout.fragment_step)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}