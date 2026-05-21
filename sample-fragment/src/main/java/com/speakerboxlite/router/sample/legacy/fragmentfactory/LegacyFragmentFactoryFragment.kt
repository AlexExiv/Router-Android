package com.speakerboxlite.router.sample.legacy.fragmentfactory

import android.os.Bundle
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.RouterFragment
import com.speakerboxlite.router.sample.base.BaseFragment
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.modules.AppData
import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.samplefragment.databinding.FragmentLegacyFragmentFactoryBinding
import javax.inject.Inject

data class LegacyFragmentFactoryPath(
    val title: String,
    val source: String): RoutePath

@RouterFragment(
    path = LegacyFragmentFactoryPath::class,
    component = AppComponent::class)
class LegacyFragmentFactoryFragment: BaseFragment<FragmentLegacyFragmentFactoryBinding>(R.layout.fragment_legacy_fragment_factory)
{
    @Inject
    lateinit var appData: AppData

    override fun onBindData()
    {
        super.onBindData()

        dataBinding.title.text = requireArguments().getString(ARG_TITLE)
        dataBinding.subtitle.text = requireArguments().getString(ARG_SOURCE)
        dataBinding.injected.text = "Injected AppData: ${appData.appString}"
    }

    companion object
    {
        private const val ARG_TITLE = "title"
        private const val ARG_SOURCE = "source"

        fun newInstance(path: LegacyFragmentFactoryPath): LegacyFragmentFactoryFragment =
            LegacyFragmentFactoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, path.title)
                    putString(ARG_SOURCE, "Factory source: ${path.source}")
                }
            }
    }
}
