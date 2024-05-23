package com.speakerboxlite.router.compose.bootstrap

import com.speakerboxlite.router.ViewBTS
import com.speakerboxlite.router.ViewDialog
import com.speakerboxlite.router.compose.ViewCompose
import java.util.UUID

abstract class BaseViewCompose: ViewCompose
{
    override var viewKey: String = UUID.randomUUID().toString()
}

abstract class DialogCompose: BaseViewCompose(), ViewDialog

abstract class BottomSheetCompose: BaseViewCompose(), ViewBTS