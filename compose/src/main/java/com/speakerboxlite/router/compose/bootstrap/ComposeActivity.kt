package com.speakerboxlite.router.compose.bootstrap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.speakerboxlite.router.BaseHostView
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.compose.LocalRouterManager

abstract class ComposeActivity(@LayoutRes layoutId: Int): ComponentActivity(layoutId), BaseHostView
{
    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    constructor(): this(0)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalRouterManager provides routerManager) {
                Content()
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            router.topRouter?.back()
        }
    }

    @Composable
    abstract fun Content()
}