package com.speakerboxlite.router.compose.base

import com.speakerboxlite.router.compose.ViewCompose
import java.util.UUID

abstract class BaseViewCompose: ViewCompose
{
    override var viewKey: String = UUID.randomUUID().toString()
}