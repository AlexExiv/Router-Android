package com.speakerboxlite.router.compose

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.findFragment

@Composable
fun FragmentContainer(modifier: Modifier = Modifier,
                      commit: FragmentTransaction.(containerId: Int) -> Unit)
{
    val localView = LocalView.current
    // Find the parent fragment, if one exists. This will let us ensure that
    // fragments inflated via a FragmentContainerView are properly nested
    // (which, in turn, allows the fragments to properly save/restore their state)
    val parentFragment = remember(localView) {
        try
        {
            localView.findFragment<Fragment>()
        }
        catch (e: IllegalStateException)
        {
            null// findFragment throws if no parent fragment is found
        }
    }

    val containerId by rememberSaveable { mutableStateOf(View.generateViewId()) }
    val container = remember { mutableStateOf<FragmentContainerView?>(null) }
    val viewBlock: (Context) -> View = remember(localView) {
        {
            context ->

            FragmentContainerView(context)
                .apply { id = containerId }
                .also {
                    val fragmentManager = parentFragment?.childFragmentManager ?: (context as? FragmentActivity)?.supportFragmentManager
                    fragmentManager?.commit { commit(it.id) }
                    container.value = it
                }
        }
    }

    AndroidView(modifier = modifier, factory = viewBlock, update = {})

    // Set up a DisposableEffect that will clean up fragments when the FragmentContainer is disposed
    val localContext = LocalContext.current
    DisposableEffect(localView, localContext, container) {
        onDispose {
            val fragmentManager = parentFragment?.childFragmentManager ?: (localContext as? FragmentActivity)?.supportFragmentManager // Now find the fragment inflated via the FragmentContainerView
            val existingFragment = fragmentManager?.findFragmentById(container.value?.id ?: 0)

            if (existingFragment != null && !fragmentManager.isStateSaved)
            {
                // If the state isn't saved, that means that some state change
                // has removed this Composable from the hierarchy
                fragmentManager.commit {
                    remove(existingFragment)
                }
            }
        }
    }
}