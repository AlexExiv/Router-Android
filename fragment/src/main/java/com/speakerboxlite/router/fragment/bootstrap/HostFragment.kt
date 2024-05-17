package com.speakerboxlite.router.fragment.bootstrap

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.fragment.ext._viewKey

open class HostFragment(@LayoutRes layoutId: Int): Fragment(layoutId), HostView
{
    override var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    constructor(): this(0)
}