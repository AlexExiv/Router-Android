package com.speakerboxlite.router

import com.speakerboxlite.router.result.RouterResultProvider

interface View
{
    var viewKey: String
}

interface ViewFragment: View
{
    var router: Router
    var localRouter: RouterLocal

    var resultProvider: RouterResultProvider
}

interface ViewVM<VM: ViewModel>
{
    var viewModel: VM
}

interface ViewFragmentVM<VM: ViewModel>: ViewFragment, ViewVM<VM>

interface ViewTabs
{
    var routerTabs: RouterTabs
}

interface ViewDialog

interface ViewBTS
