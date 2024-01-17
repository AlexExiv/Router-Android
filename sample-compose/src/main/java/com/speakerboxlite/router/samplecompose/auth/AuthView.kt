package com.speakerboxlite.router.samplecompose.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.speakerboxlite.router.compose.routerViewModel
import com.speakerboxlite.router.samplecompose.base.BaseViewCompose

class AuthView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Auth(viewModel = routerViewModel(view = this))
    }

}

@Composable
fun Auth(viewModel: AuthViewModel)
{
    Box(modifier = Modifier.fillMaxSize()) {
        Row { 
            Column {
                Text(text = "You have to sign in to use it")

                Button(onClick = { viewModel.onSignIn() }) {
                    Text(text = "I'm in!")
                }
            }
        }
    }
}
