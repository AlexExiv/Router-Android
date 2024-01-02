package com.speakerboxlite.router.sample.composite

import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.composite.details.DetailsViewModel
import com.speakerboxlite.router.sample.di.AppComponent
import com.speakerboxlite.router.sample.di.MasterDetailScope
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named

class CompositeData
{
    val sharedVar = MutableLiveData("")
}

@MasterDetailScope
@Component(dependencies = [AppComponent::class], modules = [CompositeModule::class])
interface CompositeComponent: com.speakerboxlite.router.controllers.Component
{
    fun inject(vm: CompositeViewModel)
    fun inject(vm: DetailsViewModel)
}

@Module
class CompositeModule(val compositeData: CompositeData)
{
    @Provides
    @MasterDetailScope
    @Named("sharedVar")
    fun provideSharedVar() = compositeData.sharedVar
}