package com.speakerboxlite.router.samplemixed.step.fragment

import com.speakerboxlite.router.samplemixed.R
import com.speakerboxlite.router.samplemixed.base.fragment.BaseViewModelFragment
import com.speakerboxlite.router.samplemixed.databinding.FragmentStepBinding
import com.speakerboxlite.router.samplemixed.step.StepViewModel

class StepFragment: BaseViewModelFragment<StepViewModel, FragmentStepBinding>(R.layout.fragment_step)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}