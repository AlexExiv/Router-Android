package com.speakerboxlite.router.sample.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterLocal
import com.speakerboxlite.router.ViewVM
import com.speakerboxlite.router.result.RouterResultProvider

abstract class BaseViewModelDialogFragment<VM: BaseViewModel, VDB: ViewDataBinding>(open val layoutId: Int): DialogFragment(), ViewVM<VM>
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

    override fun onDismiss(dialog: DialogInterface)
    {
        super.onDismiss(dialog)
    }

    open fun onBindData()
    {

    }
}