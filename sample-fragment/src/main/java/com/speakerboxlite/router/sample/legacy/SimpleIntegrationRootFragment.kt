package com.speakerboxlite.router.sample.legacy

import com.speakerboxlite.router.annotations.RouterFragmentRoute
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.samplefragment.databinding.FragmentSimpleIntegrationRootBinding

@RouterFragmentRoute(
    path = SimpleIntegrationRootPath::class,
    viewModel = SimpleIntegrationRootViewModel::class,
    component = AppComponent::class)
class SimpleIntegrationRootFragment: BaseViewModelFragment<SimpleIntegrationRootViewModel, FragmentSimpleIntegrationRootBinding>(R.layout.fragment_simple_integration_root)
{
    override fun onBindData()
    {
        super.onBindData()

        dataBinding.viewmodel = viewModel
        dataBinding.title.text = requireArguments().getString("title")
        dataBinding.subtitle.text = requireArguments().getString("source")
    }
}
