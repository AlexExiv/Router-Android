package com.speakerboxlite.router.sample.shared.subs.sub1

import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.sample.base.BaseViewModelBottomFragment
import com.speakerboxlite.router.samplefragment.databinding.FragmentSharedSub1Binding

class SharedSub1Fragment: BaseViewModelBottomFragment<SharedSub1ViewModel, FragmentSharedSub1Binding>(R.layout.fragment_shared_sub_1)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}