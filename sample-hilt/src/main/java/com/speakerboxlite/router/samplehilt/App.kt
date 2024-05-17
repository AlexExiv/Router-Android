package com.speakerboxlite.router.samplehilt

import com.speakerboxlite.router.compose.bootstrap.ComposeApplication
import com.speakerboxlite.router.samplehilt.base.animations.AnimationControllerComposeSlide
import com.speakerboxlite.router.samplehilt.di.AppComponent
import com.speakerboxlite.router.samplehilt.main.MainPath
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: ComposeApplication<RouterComponentImpl>()
{
    lateinit var component: AppComponent // Shared component for the Middleware controllers

    override fun onCreateComponent()
    {
        super.onCreateComponent()
        // Create it like the Hilt's documentation says
        component = EntryPoints.get(this, AppComponent::class.java)
    }

    override fun onCreateRouter()
    {
        routerComponent = RouterComponentImpl()
        routerComponent.initialize(MainPath(), { _, _ -> AnimationControllerComposeSlide() }, component)
    }
}