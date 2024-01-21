package com.speakerboxlite.router.samplemixed.di.modules

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

data class AppData(val appString: String)

@Module
class AppModule(val appData: AppData)
{
    @Provides
    @Singleton
    fun provideAppData(): AppData = appData
}