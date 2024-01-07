package com.speakerboxlite.router.samplemixed

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.samplemixed.ui.theme.RouterTheme
import com.speakerboxlite.router.BaseHostView
import com.speakerboxlite.router.compose.ComposeHostViewRoot
import com.speakerboxlite.router.fragmentcompose.ComposeNavigatorMixed
import com.speakerboxlite.router.samplemixed.base.fragment.ComposeHostFragment

class MainActivity : AppCompatActivity(), BaseHostView
{
    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    var root: ComposeHostViewRoot = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            RouterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    root.value?.invoke()
                }
            }
        }
    }

    override fun onStart()
    {
        super.onStart()
        root.value = {
            ComposeNavigatorMixed(router = router, fragmentHostFactory = { ComposeHostFragment() })
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed()
    {
        //super.onBackPressed()
        router.topRouter?.back()
    }
}
