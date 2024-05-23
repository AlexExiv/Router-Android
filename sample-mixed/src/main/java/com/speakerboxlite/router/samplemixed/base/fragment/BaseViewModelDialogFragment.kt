package com.speakerboxlite.router.samplemixed.base.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.speakerboxlite.router.fragment.bootstrap.DialogFragmentViewModel
import com.speakerboxlite.router.samplemixed.base.BaseViewModel

abstract class BaseViewModelDialogFragment<VM: BaseViewModel, VDB: ViewDataBinding>(open val layoutId: Int):
    DialogFragmentViewModel<VM>()
{
    lateinit var dataBinding: VDB

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), layoutId, null, false)
        dataBinding.lifecycleOwner = this
        onViewCreated(dataBinding.root, null)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dataBinding.root)
            .setCancelable(false)
            .create()

        //It's important for mix (compose and fragment) cases to turn off touch outside event because
        //It's impossible to catch the close event and close the host  fragment
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        onBindData()
    }

    open fun onBindData()
    {

    }
}