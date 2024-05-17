package com.speakerboxlite.router.samplecompose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.samplecompose.ui.theme.RouterTheme
import com.speakerboxlite.router.compose.bootstrap.ComposeActivity

class MainActivity: ComposeActivity()
{
    /**
     * Root content. It's entry point for your compose view
     */
    @Composable
    override fun Content()
    {
        RouterTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                ComposeNavigator(router = router)
            }
        }
    }
}
