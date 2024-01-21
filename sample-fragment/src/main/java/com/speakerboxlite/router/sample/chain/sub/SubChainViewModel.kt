package com.speakerboxlite.router.sample.chain.sub

import android.app.Application
import com.speakerboxlite.router.sample.base.BaseViewModel

class SubChainViewModel(val step: Int, app: Application): BaseViewModel(app)
{
    val title = "I'm a sub route of the chain's step $step"

    fun onClose()
    {
        resultProvider.send(step)
        router.close()
    }
}