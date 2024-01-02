package com.speakerboxlite.router.sample.auth

import com.speakerboxlite.router.sample.R
import com.speakerboxlite.router.sample.base.BaseViewModelFragment
import com.speakerboxlite.router.sample.databinding.FragmentAuthBinding

class AuthFragment: BaseViewModelFragment<AuthViewModel, FragmentAuthBinding>(R.layout.fragment_auth)
{
    override fun onBindData()
    {
        super.onBindData()
        dataBinding.viewmodel = viewModel
    }
}