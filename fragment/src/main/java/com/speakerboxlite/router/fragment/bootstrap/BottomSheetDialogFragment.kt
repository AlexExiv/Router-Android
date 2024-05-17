package com.speakerboxlite.router.fragment.bootstrap

import androidx.annotation.LayoutRes
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterLocal
import com.speakerboxlite.router.ViewBTS
import com.speakerboxlite.router.fragment.ViewFragment
import com.speakerboxlite.router.fragment.ext._viewKey
import com.speakerboxlite.router.result.RouterResultProvider

open class BottomSheetDialogFragment(@LayoutRes layoutId: Int): com.google.android.material.bottomsheet.BottomSheetDialogFragment(layoutId),
    ViewFragment, ViewBTS
{
    override var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override lateinit var router: Router
    override lateinit var localRouter: RouterLocal
    override lateinit var resultProvider: RouterResultProvider

    constructor(): this(0)
}