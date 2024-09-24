package com.speakerboxlite.router.samplemixed.mixed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.speakerboxlite.router.Result
import com.speakerboxlite.router.RouterResultDispatcher
import com.speakerboxlite.router.compose.CompositionLocalRouterPreview
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.bootstrap.BaseViewCompose
import com.speakerboxlite.router.compose.currentOrThrow
import com.speakerboxlite.router.compose.rememberResultCallback
import com.speakerboxlite.router.compose.routerViewModel
import com.speakerboxlite.router.fragmentcompose.ComposeNavigatorLocalMixed
import com.speakerboxlite.router.samplemixed.mixed.compose.MixedInComposePath
import com.speakerboxlite.router.samplemixed.mixed.fragment.MixedInFragmentPath
import com.speakerboxlite.router.samplemixed.mixed.fragment.MixedInFragmentViewModel

class MixedComposeView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        MixedCompose(viewModel = routerViewModel(view = this))
    }
}

@Composable
fun MixedCompose(viewModel: MixedComposeViewModel)
{
    Surface(modifier = Modifier.fillMaxSize()) {

        Column {

            val fragmentPath = remember { MixedInFragmentPath() }
            val fragmentResultCallback = rememberResultCallback<MixedComposeViewModel, Int> { it.vr.onDispatchFragmentResult(it.result) }
            ComposeNavigatorLocalMixed(
                viewResult = viewModel,
                path = fragmentPath,
                result = fragmentResultCallback)

            val composePath = remember { MixedInComposePath() }
            val composeResultCallback = rememberResultCallback<MixedComposeViewModel, Int> { it.vr.onDispatchComposeResult(it.result) }
            ComposeNavigatorLocalMixed(
                viewResult = viewModel,
                path = composePath,
                result = composeResultCallback)

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "I'm a Root compose text")

            val fragmentStep by viewModel.fragmentStep.collectAsState(initial = 0)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "I'm a result from mixed in fragment: $fragmentStep")

            val fragmentStepDI by viewModel.mixedFragment.observeAsState(initial = 0)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "I'm a DI result from mixed in fragment: $fragmentStepDI")

            val composeStep by viewModel.composeStep.collectAsState(initial = 0)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "I'm a result from mixed in compose: $composeStep")

            val composeStepDI by viewModel.mixedCompose.observeAsState(initial = 0)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "I'm a DI result from mixed in compose: $composeStepDI")
        }
    }
}

@Preview
@Composable
fun MixedComposePreview()
{
    CompositionLocalRouterPreview {
        ComposeNavigatorLocalMixed(path = MixedInComposePath())
    }
}