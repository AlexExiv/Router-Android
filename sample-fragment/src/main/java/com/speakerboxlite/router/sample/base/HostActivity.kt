package com.speakerboxlite.router.sample.base

import android.content.Intent
import android.content.pm.ActivityInfo
import com.speakerboxlite.router.fragment.bootstrap.FragmentActivity
import com.speakerboxlite.router.samplefragment.R
import java.io.Serializable

open class BaseHostActivity: FragmentActivity(R.layout.activity_host)
{
    override fun createIntent(params: Serializable?): Intent
    {
        val p = params as? RouteStyle
        return when (p)
        {
            RouteStyle.Landscape -> Intent(this, HostLandscapeActivity::class.java)
            else -> Intent(this, HostActivity::class.java)
        }
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