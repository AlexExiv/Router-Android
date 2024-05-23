package com.speakerboxlite.router.sample.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.speakerboxlite.router.RouterTabs
import com.speakerboxlite.router.ViewTabs
import com.speakerboxlite.router.fragment.ViewFragmentVM
import com.speakerboxlite.router.fragment.bootstrap.Fragment
import com.speakerboxlite.router.fragment.bootstrap.FragmentViewModel
import com.speakerboxlite.router.samplefragment.R

abstract class BaseViewModelFragment<VM: BaseViewModel, VDB: ViewDataBinding>(@LayoutRes val layoutId: Int,
                                                                              @MenuRes val menuId: Int = 0): FragmentViewModel<VM>(layoutId), ViewFragmentVM<VM>
{
    lateinit var dataBinding: VDB

    protected val isShowBackBtn get() = router.hasPreviousScreen
    protected var toolbar: Toolbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View?
    {
        dataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        dataBinding.lifecycleOwner = this

        return dataBinding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)

        if (isViewModelInjected)
            onBindData()

        if (toolbar != null)
        {
            invalidateMenu()

            if (isShowBackBtn)
            {
                toolbar?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
                toolbar?.setNavigationOnClickListener { requireActivity().onBackPressed() }
            }
        }
    }

    open fun onBindData()
    {

    }

    protected fun invalidateMenu()
    {
        if (menuId != 0)
        {
            toolbar?.inflateMenu(menuId)
            val menu = toolbar?.menu
            if (menu != null)
                onConfigureMenu(menu)
        }

        toolbar?.setOnMenuItemClickListener { onOptionsItemSelected(it) }
    }

    protected open fun onConfigureMenu(menu: Menu)
    {

    }
}

abstract class BaseViewModelTabFragment<VM: BaseViewModel, VDB: ViewDataBinding>(
    @LayoutRes layoutId: Int,
    @MenuRes  menuId: Int = 0): BaseViewModelFragment<VM, VDB>(layoutId, menuId), ViewTabs
{
    override lateinit var routerTabs: RouterTabs
}