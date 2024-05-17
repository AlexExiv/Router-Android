package com.speakerboxlite.router.fragment.bootstrap

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.speakerboxlite.router.BaseHostView
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.IntentBuilder
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.fragment.HostClosableActivity
import com.speakerboxlite.router.fragment.IHostClosableActivity
import java.io.Serializable

abstract class FragmentActivity(@LayoutRes layoutId: Int): AppCompatActivity(layoutId), BaseHostView,
    HostActivityFactory, IHostClosableActivity by HostClosableActivity()
{
    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    constructor(): this(0)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        withActivity(this)

        onBackPressedDispatcher.addCallback(this) {
            router.topRouter?.back()
        }
    }

    override fun startActivity(params: Serializable?, builder: IntentBuilder)
    {
        val intent = createIntent(params)
        builder(intent)
        startActivity(intent)
    }

    open fun createIntent(params: Serializable?): Intent
    {
        error("Has to be implemented to create new host activities")
    }
}