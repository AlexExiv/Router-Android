package com.speakerboxlite.router

import com.speakerboxlite.router.result.RouterResultProvider

interface View
{
    var viewKey: String
}

interface ViewResult
{
    var resultProvider: RouterResultProvider
}

interface ViewVM<VM: ViewModel>
{
    var viewModel: VM
}

interface ViewTabs
{
    var routerTabs: RouterTabs
}

interface ViewDialog

interface ViewBTS

interface BaseHostView
{
    var routerManager: RouterManager
    var router: Router
}