package com.speakerboxlite.router.sample.base

import androidx.appcompat.app.AppCompatActivity
import com.speakerboxlite.router.lifecycle.BaseHostView
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.sample.R

class HostActivity: AppCompatActivity(R.layout.activity_host), BaseHostView
{
    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    override fun onBackPressed()
    {
        //super.onBackPressed()
        router.topRouter?.back()
    }
}