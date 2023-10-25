package com.speakerboxlite.router.sample.composite

import android.os.Bundle
import android.view.View
import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.composite.details.DetailsPath
import com.speakerboxlite.router.sample.databinding.FragmentCompositeBinding

class CompositeFragment: BaseViewModelFragment<CompositeViewModel, FragmentCompositeBinding>(R.layout.fragment_composite)
{
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        localRouter?.routeInContainerWithResult<String>(R.id.sub, DetailsPath()) { viewModel.onChangeValue(it) }
    }

    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}