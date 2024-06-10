package com.speakerboxlite.router.sample

import com.speakerboxlite.router.fragment.bootstrap.FragmentApplication
import com.speakerboxlite.router.sample.base.animations.AnimationControllerDefault
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.DaggerAppComponent
import com.speakerboxlite.router.sample.di.modules.AppData
import com.speakerboxlite.router.sample.di.modules.AppModule
import com.speakerboxlite.router.sample.di.modules.UserData
import com.speakerboxlite.router.sample.di.modules.UserModule
import com.speakerboxlite.router.sample.main.MainPath
import com.speakerboxlite.router.sample.tabs.TabsPath

class App: FragmentApplication<RouterComponentImpl>()
{
    lateinit var component: AppComponent

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
        routerComponent.initialize(TabsPath(), { _, _ -> AnimationControllerDefault() }, component)
    }
}