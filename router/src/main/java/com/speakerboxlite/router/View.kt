package com.speakerboxlite.router

typealias ViewModelFactory<VM> = () -> VM

interface View<VM: ViewModel>
{
    var viewKey: String

    var router: Router
    var localRouter: RouterLocal

    var viewModel: VM
    //var viewModelFactory: ViewModelFactory<VM>?
}