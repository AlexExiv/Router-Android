package com.speakerboxlite.router

import com.speakerboxlite.router.result.RouterResultProvider

interface View
{
    var viewKey: String

    var router: Router?
    var localRouter: RouterLocal?

    var resultProvider: RouterResultProvider
}

interface ViewVM<VM: ViewModel>: View
{
    var viewModel: VM
}