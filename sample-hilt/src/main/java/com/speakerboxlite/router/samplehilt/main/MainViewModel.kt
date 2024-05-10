package com.speakerboxlite.router.samplehilt.main

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.asFlow
import com.speakerboxlite.router.samplehilt.base.BaseViewModel
import com.speakerboxlite.router.samplehilt.di.modules.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    var userData: UserData,
    app: Application): BaseViewModel(app)
{
    var authText by mutableStateOf("")

    override fun onInit()
    {
        super.onInit()

        val uiScope = CoroutineScope(Dispatchers.Main)
        userData.isLogin.asFlow().onEach { authText = if (it) "You are a authorized user" else "You are a guest" }.launchIn(uiScope)
    }
}