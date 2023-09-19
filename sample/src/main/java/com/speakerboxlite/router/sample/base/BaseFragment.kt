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
import androidx.fragment.app.Fragment
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterLocal
import com.speakerboxlite.router.View
import com.speakerboxlite.router.result.RouterResultProvider
import com.speakerboxlite.router.sample.R

abstract class BaseFragment<VDB: ViewDataBinding>(@LayoutRes val layoutId: Int,
                                                  @MenuRes val menuId: Int = 0): Fragment(), View
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
    override lateinit var resultProvider: RouterResultProvider

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