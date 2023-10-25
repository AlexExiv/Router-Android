package com.speakerboxlite.router.sample.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.sample.R

class HostFragment: Fragment(R.layout.fragment_host), HostView
{
    override var viewKey: String
        get() = requireArguments().getString("VIEW_KEY")!!
        set(value)
        {
            if (arguments == null)
                arguments = Bundle()
            requireArguments().putString("VIEW_KEY", value)
        }

    override var router: Router? = null
}