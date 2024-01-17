package com.speakerboxlite.router.samplemixed.dialog.fragment

import com.speakerboxlite.router.samplemixed.R
import com.speakerboxlite.router.samplemixed.base.fragment.BaseViewModelDialogFragment
import com.speakerboxlite.router.samplemixed.databinding.FragmentDialogBinding

class DialogFragment: BaseViewModelDialogFragment<DialogViewModel, FragmentDialogBinding>(R.layout.fragment_dialog)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}