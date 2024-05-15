package com.speakerboxlite.router.samplemixed.base.fragment

import android.content.Intent
import android.content.pm.ActivityInfo
import com.speakerboxlite.router.fragment.bootstrap.FragmentActivity
import com.speakerboxlite.router.samplemixed.R
import java.io.Serializable

open class BaseHostActivity: FragmentActivity(R.layout.activity_host)
{
    override fun createIntent(params: Serializable?): Intent = Intent(this, HostActivity::class.java)
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