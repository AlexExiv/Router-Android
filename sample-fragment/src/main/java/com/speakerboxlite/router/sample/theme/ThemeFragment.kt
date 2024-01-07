package com.speakerboxlite.router.sample.theme

import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.samplefragment.databinding.FragmentThemeBinding

class ThemeFragment: BaseViewModelFragment<ThemeViewModel, FragmentThemeBinding>(R.layout.fragment_theme)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}