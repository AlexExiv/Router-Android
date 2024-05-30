package com.speakerboxlite.router.command

import com.speakerboxlite.router.RouterSimple
import com.speakerboxlite.router.View
import com.speakerboxlite.router.controllers.AnimationController
import java.lang.ref.WeakReference

class ViewFactory(router: RouterSimple): ViewFactoryInterface
{
    val weakRouter = WeakReference(router)

    override fun createView(key: String): View?
    {
        val router = weakRouter.get() ?: return null
        val path = router.getPath(key) ?: return null
        val meta = router.viewsStackById[key]
        val route = meta?.route ?: router.findRoute(path)

        val view = route.onCreateView(path)
        view.viewKey = key
        return view
    }

    override fun createAnimation(view: View): AnimationController?
    {
        val router = weakRouter.get() ?: return null
        val path = router.getPath(view.viewKey) ?: return null
        val meta = router.viewsStackById[view.viewKey]
        val route = meta?.route ?: router.findRoute(path)

        return route.animationController(meta?.presentation, view)
    }
}