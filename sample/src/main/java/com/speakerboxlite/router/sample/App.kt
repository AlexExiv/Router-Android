package com.speakerboxlite.router.sample

import android.app.Application
import android.content.Intent
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.lifecycle.ActivityLifeCycle
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.sample.base.animations.AnimationControllerDefault
import com.speakerboxlite.router.sample.base.HostActivity
import com.speakerboxlite.router.sample.base.HostLandscapeActivity
import com.speakerboxlite.router.sample.base.RouteStyle
import com.speakerboxlite.router.sample.compose.ComposeHostFragment
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.DaggerAppComponent
import com.speakerboxlite.router.sample.di.modules.AppData
import com.speakerboxlite.router.sample.di.modules.AppModule
import com.speakerboxlite.router.sample.di.modules.UserData
import com.speakerboxlite.router.sample.di.modules.UserModule
import com.speakerboxlite.router.sample.main.MainPath
import java.io.Serializable

class App: Application(), HostActivityFactory
{
    lateinit var component: AppComponent
    val routerComponent = RouterComponentImpl()
    lateinit var lifeCycle: ActivityLifeCycle

    override fun onCreate()
    {
        super.onCreate()

        component = DaggerAppComponent.builder()
            .appModule(AppModule(AppData("App String")))
            .userModule(UserModule(UserData()))
            .build()

        routerComponent.initialize(MainPath(), AnimationControllerDefault(), component)

        lifeCycle = ActivityLifeCycle(routerComponent.routerManager, this)
        registerActivityLifecycleCallbacks(lifeCycle)
    }

    override fun create(params: Serializable?): Intent
    {
        val p = params as? RouteStyle
        return when (p)
        {
            RouteStyle.Landscape -> Intent(this, HostLandscapeActivity::class.java)
            else -> Intent(this, HostActivity::class.java)
        }
    }

    override fun createHost(): Fragment = ComposeHostFragment()
}