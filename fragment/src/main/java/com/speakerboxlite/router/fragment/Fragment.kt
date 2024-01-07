package com.speakerboxlite.router.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

var Fragment._viewKey: String
    get() = requireArguments().getString("com.speakerboxlite.router.VIEW_KEY")!!
    set(value)
    {
        if (arguments == null)
            arguments = Bundle()
        requireArguments().putString("com.speakerboxlite.router.VIEW_KEY", value)
    }