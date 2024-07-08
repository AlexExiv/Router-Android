package com.speakerboxlite.router.samplecompose

import android.util.Log
import com.speakerboxlite.router.RouterConfigGlobal
import com.speakerboxlite.router.compose.bootstrap.ComposeApplication
import com.speakerboxlite.router.samplecompose.main.MainPath
import com.speakerboxlite.router.samplecompose.base.animations.AnimationControllerComposeSlide
import com.speakerboxlite.router.samplecompose.di.AppComponent
import com.speakerboxlite.router.samplecompose.di.DaggerAppComponent
import com.speakerboxlite.router.samplecompose.di.modules.AppData
import com.speakerboxlite.router.samplecompose.di.modules.AppModule
import com.speakerboxlite.router.samplecompose.di.modules.UserData
import com.speakerboxlite.router.samplecompose.di.modules.UserModule

class App: ComposeApplication<RouterComponentImpl>()
{
    lateinit var component: AppComponent

    init
    {
        RouterConfigGlobal.restoreSingleTime = false
        RouterConfigGlobal.logFun = { t, m -> Log.d(t, m) }
    }

    override fun onCreateComponent()
    {
        super.onCreateComponent()

        component = DaggerAppComponent.builder()
            .appModule(AppModule(AppData("App String")))
            .userModule(UserModule(UserData()))
            .build()
    }

    override fun onCreateRouter()
    {
        routerComponent = RouterComponentImpl()
        routerComponent.initialize(MainPath(), { _, _ -> AnimationControllerComposeSlide() }, component)
    }
}