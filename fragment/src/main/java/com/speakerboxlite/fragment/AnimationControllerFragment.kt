package com.speakerboxlite.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.controllers.AnimationController

interface AnimationControllerFragment<Path: RoutePath, V: View>: AnimationController
{
    /**
     * Called during the `FragmentManager` transaction composition. Implement this method to configure animations for screen transitions.
     *
     * @param path       The path to the screen connected by the `RouteController`.
     * @param transaction The current transaction of the `FragmentManager`.
     * @param current    The current fragment in the container.
     * @param next       The view being added to the container.
     * @param replacing  If `true`, the view is replacing the current fragment in the container without adding to the backstack.
     * If `false`, the view is being added to the backstack.
     */
    fun onConfigureAnimation(path: Path, transaction: FragmentTransaction, current: Fragment?, next: V, replacing: Boolean)

    /**
     * This method is called after the `onViewCreated` stage in the fragment's lifecycle.
     * It allows you to configure the view in preparation for animations.
     *
     * @param path The path to the screen connected by the `RouteController`.
     * @param view The view that should be configured.
     */
    fun onConfigureView(path: Path, view: V) {}
}

typealias AnyAnimationController = AnimationControllerFragment<RoutePath, View>
