package com.speakerboxlite.router.fragment

import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterLocal
import com.speakerboxlite.router.View
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.ViewResult
import com.speakerboxlite.router.ViewVM

interface ViewFragment: View, ViewResult
{
    var router: Router
    var localRouter: RouterLocal
}

interface ViewFragmentVM<VM: ViewModel>: ViewFragment, ViewVM<VM>