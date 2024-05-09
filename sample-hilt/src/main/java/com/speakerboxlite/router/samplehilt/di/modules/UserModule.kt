package com.speakerboxlite.router.samplehilt.di.modules

import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

data class UserData(val isLogin: MutableLiveData<Boolean> = MutableLiveData(false),
                    var isPro: MutableLiveData<Boolean> = MutableLiveData(false))

@Module
@InstallIn(SingletonComponent::class)
object UserModule
{
    @Provides
    @Singleton
    fun provideUserData(): UserData = UserData()
}