package com.speakerboxlite.router.sample.shared.subs.sub0

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentSharedSub0Binding

class SharedSub0Fragment: BaseViewModelFragment<SharedSub0ViewModel, FragmentSharedSub0Binding>(R.layout.fragment_shared_sub_0)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}