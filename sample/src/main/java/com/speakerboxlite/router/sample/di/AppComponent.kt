package com.speakerboxlite.router.sample.di

import com.speakerboxlite.router.sample.chain.ChainViewModel
import com.speakerboxlite.router.sample.chain.sub.SubChainViewModel
import com.speakerboxlite.router.sample.composite.CompositeViewModel
import com.speakerboxlite.router.sample.composite.details.DetailsViewModel
import com.speakerboxlite.router.sample.dialogs.DialogViewModel
import com.speakerboxlite.router.sample.main.MainViewModel
import com.speakerboxlite.router.sample.step.StepViewModel
import com.speakerboxlite.router.sample.tabs.TabsViewModel
import com.speakerboxlite.router.sample.tabs.tab.TabViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [])
interface AppComponent
{
    fun inject(vm: MainViewModel)
    fun inject(vm: StepViewModel)

    fun inject(vm: TabsViewModel)
    fun inject(vm: TabViewModel)

    fun inject(vm: CompositeViewModel)
    fun inject(vm: DetailsViewModel)

    fun inject(vm: DialogViewModel)

    fun inject(vm: ChainViewModel)
    fun inject(vm: SubChainViewModel)
}