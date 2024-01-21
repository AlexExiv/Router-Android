package com.speakerboxlite.router.fragment

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

    override fun onCloseHost()
    {
        val f = fragment.get() ?: return

        if (f.parentFragment == null)
        {
            if (f.parentFragmentManager.backStackEntryCount > 1)
                f.parentFragmentManager.popBackStackImmediate()
            else
                f.requireActivity().finish()
        }
        else
            f.requireParentFragment().childFragmentManager.popBackStackImmediate()
    }

    override fun withFragment(fragment: Fragment)
    {
        this.fragment = WeakReference(fragment)
    }
}