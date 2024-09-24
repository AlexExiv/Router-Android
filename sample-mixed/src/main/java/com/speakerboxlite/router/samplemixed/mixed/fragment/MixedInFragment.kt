package com.speakerboxlite.router.samplemixed.mixed.fragment

import com.speakerboxlite.router.samplemixed.R
import com.speakerboxlite.router.samplemixed.base.fragment.BaseViewModelFragment
import com.speakerboxlite.router.samplemixed.databinding.FragmentMixedInBinding

class MixedInFragment: BaseViewModelFragment<MixedInFragmentViewModel, FragmentMixedInBinding>(R.layout.fragment_mixed_in)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}