package com.speakerboxlite.router.exceptions

import android.app.Activity
import android.os.Bundle
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.RouterManagerImpl
import com.speakerboxlite.router.RouterSimple
import com.speakerboxlite.router.View
import com.speakerboxlite.router.hostActivityKey

class RouterNotFoundException: IllegalArgumentException
{
    constructor(activity: Activity, routerManager: RouterManager, bundle: Bundle?):
            super(composeMessage(activity, routerManager, bundle))

    constructor(view: HostView, routerManager: RouterManager, bundle: Bundle?):
            super(composeMessage(view, routerManager, bundle))

    constructor(view: View, routerManager: RouterManager, bundle: Bundle?):
            super(composeMessage(view, routerManager, bundle))

    companion object
    {
        fun composeMessage(activity: Activity, routerManager: RouterManager, bundle: Bundle?): String
        {
            val managerImpl = routerManager as RouterManagerImpl
            val simpleTop = routerManager.top as? RouterSimple
            return "Router hasn't been found. Key: ${activity.hostActivityKey} ; " +
                    "Class name ${activity::class.simpleName} ; " +
                    "Saved Instance: ${bundle != null} ; " +
                    "Routers count: ${managerImpl.routers.size} ; " +
                    "Path to top: ${simpleTop?.buildViewStackPath() ?: listOf()}"
        }

        fun composeMessage(view: HostView, routerManager: RouterManager, bundle: Bundle?): String
        {
            val managerImpl = routerManager as RouterManagerImpl
            val simpleTop = routerManager.top as? RouterSimple
            return "Router hasn't been found. Key: ${view.viewKey} ; " +
                    "Class name ${view::class.simpleName} ; " +
                    "Saved Instance: ${bundle != null} ; " +
                    "Routers count: ${managerImpl.routerByView.size} ; " +
                    "Top router: ${simpleTop?.buildViewStackPath() ?: listOf()}"
        }

        fun composeMessage(view: View, routerManager: RouterManager, bundle: Bundle?): String
        {
            val managerImpl = routerManager as RouterManagerImpl
            val simpleTop = routerManager.top as? RouterSimple
            return "Router hasn't been found. Key: ${view.viewKey} ; " +
                    "Class name ${view::class.simpleName} ; " +
                    "Saved Instance: ${bundle != null} ; " +
                    "Routers count: ${managerImpl.routerByView.size} ; " +
                    "Top router: ${simpleTop?.buildViewStackPath() ?: listOf()}"
        }
    }
}