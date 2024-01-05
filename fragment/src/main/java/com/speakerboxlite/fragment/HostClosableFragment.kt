package com.speakerboxlite.fragment

import androidx.fragment.app.Fragment
import com.speakerboxlite.router.HostCloseable
import java.lang.ref.WeakReference

interface IHostClosableFragment: HostCloseable
{
    fun withFragment(fragment: Fragment)
}

class HostClosableFragment: IHostClosableFragment
{
    var fragment = WeakReference<Fragment>(null)

    override fun closeHost()
    {
        if (fragment.get()?.parentFragment == null)
            fragment.get()?.requireActivity()?.finish()
        else
            fragment.get()?.requireParentFragment()?.childFragmentManager?.popBackStackImmediate()
    }

    override fun withFragment(fragment: Fragment)
    {
        this.fragment = WeakReference(fragment)
    }
}