package com.speakerboxlite.router.sample.theme

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.sample.base.RouteControllerApp
import com.speakerboxlite.router.sample.base.RouteStyle
import java.io.Serializable

class ThemePath: RoutePath

@Route
abstract class ThemeRouteController: RouteControllerApp<ThemePath, ThemeViewModel, ThemeFragment>()
{
    override val params: Serializable? get() = RouteStyle.Landscape
}