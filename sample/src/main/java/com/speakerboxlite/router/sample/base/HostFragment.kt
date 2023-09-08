package com.speakerboxlite.router.sample.base

import androidx.fragment.app.Fragment
import com.speakerboxlite.router.HostView
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.sample.R

class HostFragment: Fragment(R.layout.fragment_host), HostView
{
    override lateinit var router: Router
}