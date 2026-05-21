package com.speakerboxlite.router.sample.legacy.fragmentroute

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.RouterFragmentRoute
import com.speakerboxlite.router.sample.base.BaseFragment
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.modules.AppData
import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.samplefragment.databinding.FragmentLegacyFragmentRouteBinding
import javax.inject.Inject

data class LegacyFragmentRoutePath(
    val title: String,
    val source: String): RoutePath

@RouterFragmentRoute(
    path = LegacyFragmentRoutePath::class,
    component = AppComponent::class)
class LegacyFragmentRouteFragment: BaseFragment<FragmentLegacyFragmentRouteBinding>(R.layout.fragment_legacy_fragment_route)
{
    @Inject
    lateinit var appData: AppData

    override fun onBindData()
    {
        super.onBindData()

        dataBinding.title.text = requireArguments().getString("title")
        dataBinding.subtitle.text = "Bundle source: ${requireArguments().getString("source")}"
        dataBinding.injected.text = "Injected AppData: ${appData.appString}"
    }
}
