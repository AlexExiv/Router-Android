package com.speakerboxlite.router.controllers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View

interface AnimationController<Path: RoutePath, V: View>
{
    fun onConfigureAnimation(path: Path, transaction: FragmentTransaction, current: Fragment?, next: V, replacing: Boolean)
}

typealias AnyAnimationController = AnimationController<RoutePath, View>
