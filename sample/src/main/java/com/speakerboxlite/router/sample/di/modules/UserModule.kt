package com.speakerboxlite.router.sample.di.modules

import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

data class UserData(val isLogin: MutableLiveData<Boolean> = MutableLiveData(false),
                    var isPro: MutableLiveData<Boolean> = MutableLiveData(false))

@Module
class UserModule(val userData: UserData)
{
    @Provides
    @Singleton
    fun provideUserData(): UserData = userData
}