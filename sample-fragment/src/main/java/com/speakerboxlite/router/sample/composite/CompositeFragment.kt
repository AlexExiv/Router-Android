package com.speakerboxlite.router.sample.composite

import android.os.Bundle
import android.view.View
import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.composite.details.DetailsPath
import com.speakerboxlite.router.samplefragment.databinding.FragmentCompositeBinding

class CompositeFragment: BaseViewModelFragment<CompositeViewModel, FragmentCompositeBinding>(R.layout.fragment_composite)
{
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        localRouter.routeInContainerWithResult(viewModel, R.id.sub, DetailsPath()) { it.vr.onChangeValue(it.result) }
    }

    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}