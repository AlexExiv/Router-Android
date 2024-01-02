package com.speakerboxlite.router.sample.composite.details

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentDetailsBinding

class DetailsFragment: BaseViewModelFragment<DetailsViewModel, FragmentDetailsBinding>(R.layout.fragment_details)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}