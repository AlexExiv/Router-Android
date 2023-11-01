package com.speakerboxlite.router.sample.base.animations

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.controllers.AnyAnimationController
import com.speakerboxlite.router.sample.R

class AnimationControllerDefault: AnyAnimationController
{
    override fun onConfigureAnimation(path: RoutePath, transaction: FragmentTransaction, current: Fragment?, next: View, replacing: Boolean)
    {
        transaction.setCustomAnimations(
            if (current == null) 0 else R.anim.def_fragment_in,
            if (current == null) 0 else R.anim.def_fragment_exit,
            R.anim.def_fragment_pop_enter,
            R.anim.def_fragment_out)
    }

    override fun onConfigureView(path: RoutePath, view: View)
    {
        super.onConfigureView(path, view)
    }
}