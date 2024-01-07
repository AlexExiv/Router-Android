package com.speakerboxlite.router.sample.dialogs

import com.speakerboxlite.router.samplefragment.R
import com.speakerboxlite.router.sample.base.BaseViewModelDialogFragment
import com.speakerboxlite.router.samplefragment.databinding.FragmentDialogBinding

class DialogFragment: BaseViewModelDialogFragment<DialogViewModel, FragmentDialogBinding>(R.layout.fragment_dialog)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}