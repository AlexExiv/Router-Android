package com.speakerboxlite.router.sample.dialogs

import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.sample.base.RouteControllerApp

data class DialogPath(val title: String = "",
                      val message: String = "",
                      val okBtn: String = "",
                      val cancelBtn: String = ""): RoutePathResult<Boolean>

@Route
abstract class DialogRouteController: RouteControllerApp<DialogPath, DialogViewModel, DialogFragment>()
{
    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: DialogPath): DialogViewModel =
        modelProvider.getViewModel { DialogViewModel(path.title, path.message, path.okBtn, path.cancelBtn, it) }
}
