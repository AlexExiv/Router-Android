package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.speakerboxlite.router.Router

@Composable
fun ComposeViewEffect(stackEntry: StackEntry?, router: Router)
{
    DisposableEffect(key1 = stackEntry)
    {

        stackEntry?.also {
            if (it.view !is FragmentContainerView)
                router.onPrepareView(it.view)
        }

        onDispose { }
    }
}

@Composable
fun CompleteTransitionEffect(stackEntry: StackEntry?, navigator: ComposeNavigator)
{
    DisposableEffect(key1 = stackEntry) {

        onDispose {
            navigator.completeTransition()
        }
    }
}
