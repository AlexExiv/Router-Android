package com.speakerboxlite.router.sample.subtabs

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.base.BaseViewModel

class SubTabsViewModel(app: Application): BaseViewModel(app)
{
    val selectedTab = MutableLiveData(0)

    fun onChangeTab(tab: Int)
    {
        selectedTab.postValue(tab)
    }
}