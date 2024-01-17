package com.speakerboxlite.router.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
fun CompleteTransitionEffect(stackEntry: StackEntry?, navigator: ComposeNavigator)
{
    DisposableEffect(key1 = stackEntry) {

        onDispose {
            navigator.completeTransition()
        }
    }
}
