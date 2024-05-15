package com.speakerboxlite.router.samplemixed.base.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.speakerboxlite.router.fragment.ViewFragmentVM
import com.speakerboxlite.router.fragment.bootstrap.BottomSheetDialogFragment
import com.speakerboxlite.router.samplemixed.base.BaseViewModel

abstract class BaseViewModelBottomFragment<VM: BaseViewModel, VDB: ViewDataBinding>(open val layoutId: Int):
    BottomSheetDialogFragment(), ViewFragmentVM<VM>
{
    override lateinit var viewModel: VM

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