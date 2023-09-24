package com.speakerboxlite.router.sample.di

import com.speakerboxlite.router.sample.chain.ChainViewModel
import com.speakerboxlite.router.sample.chain.sub.SubChainViewModel
import com.speakerboxlite.router.sample.composite.CompositeViewModel
import com.speakerboxlite.router.sample.composite.details.DetailsViewModel
import com.speakerboxlite.router.sample.di.modules.AppModule
import com.speakerboxlite.router.sample.dialogs.DialogViewModel
import com.speakerboxlite.router.sample.main.MainViewModel
import com.speakerboxlite.router.sample.simple.component.SimpleComponentFragment
import com.speakerboxlite.router.sample.step.StepViewModel
import com.speakerboxlite.router.sample.step.replace.ReplaceViewModel
import com.speakerboxlite.router.sample.tabs.TabsViewModel
import com.speakerboxlite.router.sample.tabs.tab.TabViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent: com.speakerboxlite.router.controllers.Component
{
    fun inject(vm: MainViewModel)

    fun inject(vm: StepViewModel)
    fun inject(vm: ReplaceViewModel)

    fun inject(vm: TabsViewModel)
    fun inject(vm: TabViewModel)

    fun inject(vm: DialogViewModel)

    fun inject(vm: ChainViewModel)
    fun inject(vm: SubChainViewModel)

    fun inject(view: SimpleComponentFragment)
}