package com.speakerboxlite.router.sample.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterLocal
import com.speakerboxlite.router.ViewBTS
import com.speakerboxlite.router.fragment.ViewFragmentVM
import com.speakerboxlite.router.result.RouterResultProvider

abstract class BaseViewModelBottomFragment<VM: BaseViewModel, VDB: ViewDataBinding>(open val layoutId: Int): BottomSheetDialogFragment(),
    ViewFragmentVM<VM>, ViewBTS
{
    override var viewKey: String
        get() = requireArguments().getString("VIEW_KEY")!!
        set(value)
        {
            if (arguments == null)
                arguments = Bundle()
            requireArguments().putString("VIEW_KEY", value)
        }

    override lateinit var router: Router
    override lateinit var localRouter: RouterLocal
    override lateinit var viewModel: VM
    override lateinit var resultProvider: RouterResultProvider

    lateinit var dataBinding: VDB

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int)
    {
        super.setupDialog(dialog, style)

        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), layoutId, null, false)
        dataBinding.lifecycleOwner = this

        dialog.window?.decorView?.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        dialog.setContentView(dataBinding.root)

        onBindData()
    }

    open fun onBindData()
    {

    }
}