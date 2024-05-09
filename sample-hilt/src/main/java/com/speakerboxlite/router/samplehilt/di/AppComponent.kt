package com.speakerboxlite.router.samplehilt.di

import com.speakerboxlite.router.samplehilt.di.modules.AppData
import com.speakerboxlite.router.samplehilt.di.modules.UserData
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppComponent: com.speakerboxlite.router.controllers.Component
{
    fun provideAppData(): AppData
    fun provideUserData(): UserData
}