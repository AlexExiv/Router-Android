package com.speakerboxlite.router.sample.shared

import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.MasterDetailScope
import com.speakerboxlite.router.sample.shared.subs.sub0.SharedSub0ViewModel
import com.speakerboxlite.router.sample.shared.subs.sub1.SharedSub1ViewModel
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

class SharedData
{
    val sharedVar0 = MutableLiveData("")
    val sharedVar1 = MutableLiveData("")
}

@MasterDetailScope
@Component(dependencies = [AppComponent::class], modules = [SharedModule::class])
interface SharedComponent
{
    fun inject(vm: SharedViewModel)
    fun inject(vm: SharedSub0ViewModel)
    fun inject(vm: SharedSub1ViewModel)
}

@Module
class SharedModule(val sharedData: SharedData)
{
    @Provides
    @MasterDetailScope
    @Named("sharedVar0")
    fun provideSharedData() = sharedData.sharedVar0

    @Provides
    @MasterDetailScope
    @Named("sharedVar1")
    fun provideShared1Data() = sharedData.sharedVar1
}