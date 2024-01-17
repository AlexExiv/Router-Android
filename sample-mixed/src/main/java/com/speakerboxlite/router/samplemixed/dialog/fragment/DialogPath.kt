package com.speakerboxlite.router.samplemixed.dialog.fragment

import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.annotations.Route
import com.speakerboxlite.router.fragment.AndroidViewModelProvider
import com.speakerboxlite.router.samplemixed.base.RouteControllerFragmentApp

data class DialogFragmentPath(val title: String = "",
                      val message: String = "",
                      val okBtn: String = "",
                      val cancelBtn: String = ""): RoutePathResult<Boolean>

@Route
abstract class DialogFragmentRouteController: RouteControllerFragmentApp<DialogFragmentPath, DialogViewModel, DialogFragment>()
{
    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: DialogFragmentPath): DialogViewModel =
        modelProvider.getViewModel { DialogViewModel(path.title, path.message, path.okBtn, path.cancelBtn, it) }
}
