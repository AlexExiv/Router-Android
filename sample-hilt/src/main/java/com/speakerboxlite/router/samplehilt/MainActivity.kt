package com.speakerboxlite.router.samplehilt

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.BaseHostView
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.compose.ComposeHostViewRoot
import com.speakerboxlite.router.compose.ComposeNavigator
import com.speakerboxlite.router.compose.LocalRouterManager
import com.speakerboxlite.router.samplehilt.ui.theme.RouterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), BaseHostView
{
    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    var root: ComposeHostViewRoot = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalRouterManager provides routerManager) {
                RouterTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        root.value?.invoke()
                    }
                }
            }
        }
    }

    override fun onStart()
    {
        super.onStart()
        root.value = {
            ComposeNavigator(router = router)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed()
    {
        //super.onBackPressed()
        router.topRouter?.back()
    }
}