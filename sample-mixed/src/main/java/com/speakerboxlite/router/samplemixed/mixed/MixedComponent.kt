package com.speakerboxlite.router.samplemixed.mixed

import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.di.MasterDetailScope
import com.speakerboxlite.router.samplemixed.di.AppComponent
import com.speakerboxlite.router.samplemixed.mixed.compose.MixedInComposeViewModel
import com.speakerboxlite.router.samplemixed.mixed.fragment.MixedInFragmentViewModel
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named

class MixedData
{
    val mixedFragment = MutableLiveData(0)
    val mixedCompose = MutableLiveData(0)

    fun onDispose()
    {
        println("I'm a MixedData and I'm disposed!!!")
    }
}

@MasterDetailScope
@Component(dependencies = [AppComponent::class], modules = [MixedModule::class])
interface MixedComponent: com.speakerboxlite.router.controllers.Component
{
    fun provideMixedData(): MixedData

    fun inject(vm: MixedComposeViewModel)
    fun inject(vm: MixedInComposeViewModel)
    fun inject(vm: MixedInFragmentViewModel)
}

@Module
class MixedModule(val MixedData: MixedData)
{
    @Provides
    @MasterDetailScope
    fun provideMixedDataFull(): MixedData = MixedData

    @Provides
    @MasterDetailScope
    @Named("mixedFragment")
    fun provideMixedData() = MixedData.mixedFragment

    @Provides
    @MasterDetailScope
    @Named("mixedCompose")
    fun provideMixed1Data() = MixedData.mixedCompose
}