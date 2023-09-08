package com.speakerboxlite.router.sample.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterLocal
import com.speakerboxlite.router.View

abstract class BaseViewModelFragment<VM: BaseViewModel, VDB: ViewDataBinding>(open val layoutId: Int): Fragment(), View<VM>
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

    lateinit var dataBinding: VDB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View?
    {
        dataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        dataBinding.lifecycleOwner = this

        return dataBinding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        if (!viewModel.isInit)
        {
            view.postDelayed({ viewModel.onInit() }, 10)
            viewModel.onInitRequested()
        }

        onBindData()
    }

    open fun onBindData()
    {

    }
}