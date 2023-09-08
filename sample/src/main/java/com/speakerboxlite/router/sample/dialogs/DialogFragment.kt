package com.speakerboxlite.router.sample.dialogs

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelDialogFragment
import com.speakerboxlite.router.sample.databinding.FragmentDialogBinding

class DialogFragment: BaseViewModelDialogFragment<DialogViewModel, FragmentDialogBinding>(R.layout.fragment_dialog)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}