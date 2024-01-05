package com.speakerboxlite.router.sample.base.animations

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.speakerboxlite.fragment.AnyAnimationController
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.sample.R

class AnimationControllerBottomToTop: AnyAnimationController
{
    override fun onConfigureAnimation(path: RoutePath, transaction: FragmentTransaction, current: Fragment?, next: View, replacing: Boolean)
    {
        transaction.setCustomAnimations(R.anim.bottom_to_top_in, R.anim.bottom_to_top_exit, R.anim.bottom_to_top_pop_enter, R.anim.bottom_to_top_out)
    }
}