package com.speakerboxlite.router.sample.chain

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.speakerboxlite.router.sample.base.BaseViewModel
import com.speakerboxlite.router.sample.chain.sub.SubChainPath

class ChainViewModel(val step: Int, app: Application): BaseViewModel(app)
{
    val title = "Step $step"
    val chainResult = MutableLiveData("")
    val subResult = MutableLiveData("")

    fun onNextStep()
    {
        router.route(ChainStepPath(step + 1))
    }

    fun onSubChain()
    {
        router.routeWithResult(this, ChainPath(100*step + 1)) {
            it.vr.chainResult.value = "Chain has been finished at step ${it.result}"
        }
    }

    fun onSubRoute()
    {
        router.routeWithResult(this, SubChainPath(step)) {
            it.vr.subResult.value = "Sub route result is ${it.result}"
        }
    }

    fun onClose()
    {
        router.close()
    }

    fun onCloseWithResult()
    {
        resultProvider.send(step)
        router.close()
    }
}