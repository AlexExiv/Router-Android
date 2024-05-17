package com.speakerboxlite.router.sample.base

import android.app.Application
import com.speakerboxlite.router.fragment.bootstrap.AndroidViewModel

open class BaseViewModel(app: Application): AndroidViewModel(app)
{
    /**
     * This method will be called after dependency injection
     */
    override fun onInit()
    {
        super.onInit()
    }
}