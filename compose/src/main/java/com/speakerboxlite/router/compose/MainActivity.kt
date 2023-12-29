package com.speakerboxlite.router.compose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.speakerboxlite.router.ComposeHostViewRoot
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterManager
import com.speakerboxlite.router.compose.ui.theme.RouterTheme
import com.speakerboxlite.router.lifecycle.BaseHostView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

val LocalMainScope: ProvidableCompositionLocal<CoroutineScope?> = staticCompositionLocalOf { null }

class MainActivity : ComponentActivity(), BaseHostView
{
    override lateinit var routerManager: RouterManager
    override lateinit var router: Router

    var root: ComposeHostViewRoot = mutableStateOf(null)

    val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            RouterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CompositionLocalProvider(LocalMainScope provides mainScope) {
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

    override fun onDestroy()
    {
        super.onDestroy()
        mainScope.cancel()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed()
    {
        //super.onBackPressed()
        router.topRouter?.back()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier)
{
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    RouterTheme {
        Greeting("Android")
    }
}