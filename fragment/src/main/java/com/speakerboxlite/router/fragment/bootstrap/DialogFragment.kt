package com.speakerboxlite.router.fragment.bootstrap

import android.view.KeyEvent
import androidx.annotation.LayoutRes
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterLocal
import com.speakerboxlite.router.ViewDialog
import com.speakerboxlite.router.ViewModel
import com.speakerboxlite.router.fragment.ViewFragment
import com.speakerboxlite.router.fragment.ViewFragmentVM
import com.speakerboxlite.router.fragment.ext._viewKey
import com.speakerboxlite.router.result.RouterResultProvider

open class DialogFragment(@LayoutRes layoutId: Int): androidx.fragment.app.DialogFragment(layoutId), ViewFragment, ViewDialog
{
    override var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override lateinit var router: Router
    override lateinit var localRouter: RouterLocal
    override lateinit var resultProvider: RouterResultProvider

    constructor(): this(0)

    override fun onStart()
    {
        super.onStart()

        // Handle back press by our self
        dialog!!.setOnKeyListener { dialog, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP)
            {
                router.topRouter?.back()
                return@setOnKeyListener true
            }

            return@setOnKeyListener false
        }
    }
    /* // Possible workaround related to mixed project and catching of closing Dialogs by touch outside
    override fun onCancel(dialog: DialogInterface)
    {
        super.onCancel(dialog)
        router.topRouter?.back()
    }*/
}

open class DialogFragmentViewModel<VM: ViewModel>(@LayoutRes layoutId: Int): DialogFragment(layoutId), ViewFragmentVM<VM>
{
    override lateinit var viewModel: VM

    val isViewModelInjected: Boolean get() = ::viewModel.isInitialized

    constructor(): this(0)
}