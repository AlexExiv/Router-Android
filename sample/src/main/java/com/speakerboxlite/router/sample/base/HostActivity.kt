package com.speakerboxlite.router.sample.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.speakerboxlite.router.lifecycle.BaseHostView
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.sample.R

open class BaseHostActivity: AppCompatActivity(R.layout.activity_host), BaseHostView
{
    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
    }

    override fun onBackPressed()
    {
        //super.onBackPressed()
        router.topRouter?.back()
    }
}

class HostActivity: BaseHostActivity()

class HostLandscapeActivity: BaseHostActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

    }

    override fun onStart()
    {
        super.onStart()
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    override fun onDestroy()
    {
        super.onDestroy()
    }
}