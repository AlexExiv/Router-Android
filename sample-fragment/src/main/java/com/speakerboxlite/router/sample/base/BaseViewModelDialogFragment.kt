package com.speakerboxlite.router.sample.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.speakerboxlite.router.fragment.ViewFragmentVM
import com.speakerboxlite.router.fragment.bootstrap.DialogFragment

abstract class BaseViewModelDialogFragment<VM: BaseViewModel, VDB: ViewDataBinding>(open val layoutId: Int):
    DialogFragment(), ViewFragmentVM<VM>
{
    override lateinit var viewModel: VM

    lateinit var dataBinding: VDB

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), layoutId, null, false)
        dataBinding.lifecycleOwner = this
        onViewCreated(dataBinding.root, null)

        return AlertDialog.Builder(requireActivity()).setView(dataBinding.root).create()
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