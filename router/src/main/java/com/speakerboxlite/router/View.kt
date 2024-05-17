package com.speakerboxlite.router

import com.speakerboxlite.router.result.RouterResultProvider

/**
 * Base interface of all screens you want to use in navigation
 */
interface View
{
    var viewKey: String
}

/**
 * Base interface of all classes those want to send or receive results
 */
interface ViewResult
{
    var resultProvider: RouterResultProvider
}

/**
 * Base interface of all screens with ViewModels you want to use in navigation
 */
interface ViewVM<VM: ViewModel>
{
    var viewModel: VM
}

interface ViewTabs
{
    var routerTabs: RouterTabs
}

/**
 * Base interface for all Dialog screens. Using this interface, the Router recognizes it as a Dialog screen and displays it using appropriate features.
 */
interface ViewDialog

/**
 * Base interface for all Bottom Sheet screens. Using this interface, the Router recognizes it as a Bottom Sheet screen and displays it using appropriate features.
 */
interface ViewBTS

/**
 * Base interface for all host views that display the content of the Router. This could be the MainActivity for the root of the app,
 * or the HostFragment for tabs, or the Compose Root view.
 */
interface BaseHostView
{
    var routerManager: RouterManager
    var router: Router
}