package com.speakerboxlite.router.samplehilt.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

data class AppData(val appString: String)

@Module
@InstallIn(SingletonComponent::class)
object AppModule
{
    @Singleton
    @Provides
    fun provideAppData() = AppData("App String")
}