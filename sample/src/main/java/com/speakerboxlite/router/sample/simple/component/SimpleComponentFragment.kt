package com.speakerboxlite.router.sample.simple.component

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseFragment
import com.speakerboxlite.router.sample.databinding.FragmentSimpleComponentBinding
import com.speakerboxlite.router.sample.di.modules.AppData
import javax.inject.Inject

class SimpleComponentFragment: BaseFragment<FragmentSimpleComponentBinding>(R.layout.fragment_simple_component)
{
    @Inject
    lateinit var addData: AppData

    override fun onBindData()
    {
        super.onBindData()
        dataBinding.title.text = "I was set using injector: ${addData.appString}"
    }
}