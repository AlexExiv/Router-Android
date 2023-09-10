package com.speakerboxlite.router

import com.speakerboxlite.router.result.RouterResultProvider

interface View<VM: ViewModel>
{
    var viewKey: String

    var router: Router
    var localRouter: RouterLocal

    var resultProvider: RouterResultProvider

    var viewModel: VM
}