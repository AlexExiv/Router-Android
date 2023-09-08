package com.speakerboxlite.router.sample.dialogs

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.getAndroidViewModel
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class DialogPath(val title: String = "",
                      val message: String = "",
                      val okBtn: String = "",
                      val cancelBtn: String = ""): RoutePath

@Route
abstract class DialogRouteController: RouteControllerApp<DialogPath, DialogViewModel, DialogFragment>()
{
    override fun onCreateViewModel(view: DialogFragment, path: DialogPath): DialogViewModel =
        view.getAndroidViewModel { DialogViewModel(path.title, path.message, path.okBtn, path.cancelBtn, it) }
}
