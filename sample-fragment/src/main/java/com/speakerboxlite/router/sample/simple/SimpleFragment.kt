package com.speakerboxlite.router.sample.simple

import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.sample.base.BaseFragment
import com.speakerboxlite.router.samplefragment.databinding.FragmentSimpleBinding

class SimpleFragment: BaseFragment<FragmentSimpleBinding>(R.layout.fragment_simple)
{
    override fun onBindData()
    {
        super.onBindData()

        dataBinding.title.text = requireArguments().getString("TITLE_KEY")
    }
}