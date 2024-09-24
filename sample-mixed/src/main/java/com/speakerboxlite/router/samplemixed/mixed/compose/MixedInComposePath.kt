package com.speakerboxlite.router.samplemixed.mixed.compose

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.samplemixed.mixed.RouteControllerComposeMixed

@Immutable
@Stable
class MixedInComposePath: RoutePathResult<Int>
{
    override fun equals(other: Any?): Boolean
    {
        return other is MixedInComposePath
    }

    override fun hashCode(): Int
    {
        return javaClass.hashCode()
    }
}

@Route
abstract class MixedInComposeRouteController: RouteControllerComposeMixed<MixedInComposePath, MixedInComposeViewModel, MixedInComposeView>()
