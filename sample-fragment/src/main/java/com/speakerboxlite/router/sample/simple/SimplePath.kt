package com.speakerboxlite.router.sample.simple

import android.os.Bundle
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.controllers.RouteController

data class SimplePath(val title: String): RoutePath

@Route
class SimpleRouteController: RouteController<SimplePath, SimpleFragment>()
{
    override fun onCreateView(path: SimplePath): SimpleFragment =
        SimpleFragment().apply {
            arguments = Bundle().apply {
                putString("TITLE_KEY", path.title)
            }
        }
}
