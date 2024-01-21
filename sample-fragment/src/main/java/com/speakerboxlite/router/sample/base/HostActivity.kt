package com.speakerboxlite.router.sample.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.BaseHostView
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.fragment.HostClosableActivity
import com.speakerboxlite.router.fragment.IHostClosableActivity
import com.speakerboxlite.router.IntentBuilder
import com.speakerboxlite.router.samplefragment.R
import java.io.Serializable

open class BaseHostActivity: AppCompatActivity(R.layout.activity_host), BaseHostView,
    HostActivityFactory, IHostClosableActivity by HostClosableActivity()
{
    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        withActivity(this)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed()
    {
        //super.onBackPressed()
        router.topRouter?.back()
    }

    override fun startActivity(params: Serializable?, builder: IntentBuilder)
    {
        val p = params as? RouteStyle
        val intent = when (p)
        {
            RouteStyle.Landscape -> Intent(this, HostLandscapeActivity::class.java)
            else -> Intent(this, HostActivity::class.java)
        }

        builder(intent)
        startActivity(intent)
    }
}

class StartActivity: BaseHostActivity()

class HostActivity: BaseHostActivity()

class HostLandscapeActivity: BaseHostActivity()
{
    override fun onStart()
    {
        super.onStart()
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}