package com.speakerboxlite.router.samplehilt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.compose.bootstrap.ComposeActivity
import com.speakerboxlite.router.samplehilt.ui.theme.RouterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComposeActivity()
{
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