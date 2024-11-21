package com.speakerboxlite.router.samplemixed.mixed.fragment

import androidx.compose.runtime.Immutable
import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.samplemixed.mixed.RouteControllerFragmentMixed

@Immutable
class MixedInFragmentPath: RoutePathResult<Int>
{
    override fun equals(other: Any?): Boolean
    {
        return other is MixedInFragmentPath
    }

    override fun hashCode(): Int
    {
        return javaClass.hashCode()
    }
}

@Route
abstract class MixedInRouteController: RouteControllerFragmentMixed<MixedInFragmentPath, MixedInFragmentViewModel, MixedInFragment>()
