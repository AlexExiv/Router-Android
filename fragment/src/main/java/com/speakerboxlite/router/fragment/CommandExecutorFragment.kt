package com.speakerboxlite.router.fragment

import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.speakerboxlite.router.HOST_ACTIVITY_INTENT_DATA_KEY
import com.speakerboxlite.router.HOST_ACTIVITY_KEY
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.HostCloseable
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.command.Command
import com.speakerboxlite.router.command.CommandExecutor
import com.speakerboxlite.router.command.ViewFactoryInterface
import com.speakerboxlite.router.command.getViewKey
import com.speakerboxlite.router.fragment.ext.isPopped
import com.speakerboxlite.router.fragment.ext.isRemovingRecursive
import java.io.Serializable

open class CommandExecutorFragment(
    val activity: FragmentActivity,
    @IdRes val containerId: Int,
    val fragmentManager: FragmentManager,
    val activityFactory: HostActivityFactory? = null,
    val hostCloseable: HostCloseable? = null): CommandExecutor
{
    val backstackCallback = object : OnBackStackChangedListener
    {
        override fun onBackStackChanged()
        {

        }

        override fun onBackStackChangeStarted(fragment: Fragment, pop: Boolean)
        {
            if (fragment.isRemovingRecursive && pop)
                fragment.isPopped = true
        }
    }

    protected var viewFactory: ViewFactoryInterface? = null

    override fun onBind(factory: ViewFactoryInterface?)
    {
        viewFactory = factory
        fragmentManager.addOnBackStackChangedListener(backstackCallback)
    }

    override fun onUnbind()
    {
        viewFactory = null
        fragmentManager.removeOnBackStackChangedListener(backstackCallback)
    }

    override fun execute(command: Command)
    {
        _execute(command)
    }

    override fun sync(items: List<String>): List<String>
    {
        val remove = mutableListOf<String>()
        for (i in items)
        {
            if (fragmentManager.findFragmentByTag(i) == null)
                remove.add(i)
        }

        return remove
    }

    private fun _execute(command: Command)
    {
        checkFragmentManager()

        val viewKey = command.getViewKey()
        if (viewKey != null)
        {
            executeWithView(viewKey, command)
        }
        else
        {
            when (command)
            {
                is Command.Close -> close()
                is Command.CloseTo -> closeTo(command.viewKey)
                is Command.CloseAll -> closeAll()
                is Command.CloseDialog -> closeDialog(command.viewKey)
                is Command.CloseBottomSheet -> closeBottomSheet(command.viewKey)
                is Command.ChangeTab -> command.tabChangeCallback(command.tab)
                else -> {}
            }
        }
    }

    protected fun executeWithView(viewKey: String, command: Command)
    {
        if (command is Command.StartModal)
        {
            startActivity(command.viewKey, command.params)
            return
        }

        checkNotNull(viewFactory) { "ViewFactory hasn't been set" }
        val view = viewFactory?.createView(viewKey) ?: return
        val animation = viewFactory?.createAnimation(view) as? AnimationControllerFragment<RoutePath, View>

        when (command)
        {
            is Command.Dialog -> showDialog(view)
            is Command.Push -> pushFragment(command.path, view, animation, false)
            is Command.Replace -> replaceFragment(command.path, view, animation)
            is Command.BottomSheet -> showBottomSheet(view)
            is Command.SubFragment -> showSubFragment(command.containerId, view)
            else -> {}
        }
    }

    protected fun checkFragmentManager()
    {
        fragmentManager.executePendingTransactions()
    }

    protected fun close()
    {
        if (fragmentManager.backStackEntryCount > 1)
        {
            fragmentManager.popBackStackImmediate()
        }
        else
            closeAll()
    }

    protected fun closeAll()
    {
        hostCloseable?.onCloseHost()
    }

    protected fun closeTo(key: String)
    {
        if (fragmentManager.backStackEntryCount > 1)
        {
            fragmentManager.popBackStackImmediate(key, 0)
        }
    }

    protected fun startActivity(key: String, params: Serializable?)
    {
        val af = activityFactory ?: error("You are trying to start a new activity but haven't specified factory")
        af.startActivity(params) {
            intent ->
            intent.putExtra(HOST_ACTIVITY_KEY, key)
            params?.also { intent.putExtra(HOST_ACTIVITY_INTENT_DATA_KEY, it) }
        }
    }

    protected open fun pushFragment(path: RoutePath?, view: View, animation: AnimationControllerFragment<RoutePath, View>?, replacing: Boolean)
    {
        if (view is Fragment)
        {
            val transaction = fragmentManager.beginTransaction()

            if (animation != null && path != null)
            {
                val current = fragmentManager.findFragmentById(containerId)

                transaction.setReorderingAllowed(true)
                animation.onConfigureAnimation(path, transaction, current, view, replacing)
            }

            transaction
                .replace(containerId, view, view.viewKey)
                .addToBackStack(view.viewKey)
                .commit()

            fragmentManager.executePendingTransactions()
        }
    }

    protected open fun replaceFragment(path: RoutePath, byView: View, animation: AnimationControllerFragment<RoutePath, View>?)
    {
        if (byView is Fragment)
        {
            fragmentManager.popBackStack()
            pushFragment(path, byView, animation, true)
        }
    }

    protected open fun showBottomSheet(view: View)
    {
        if (view is BottomSheetDialogFragment)
        {
            view.show(fragmentManager, view.viewKey)
            fragmentManager.executePendingTransactions()
        }
    }

    protected open fun closeBottomSheet(key: String)
    {
        val f = fragmentManager.findFragmentByTag(key)
        if (f is BottomSheetDialogFragment)
        {
            f.dismiss()
            fragmentManager.executePendingTransactions()

            if (fragmentManager.backStackEntryCount == 0)
                closeAll()
        }
    }

    protected open fun showDialog(view: View)
    {
        if (view is DialogFragment)
        {
            view.show(fragmentManager, view.viewKey)
            fragmentManager.executePendingTransactions()
        }
    }

    protected open fun closeDialog(key: String)
    {
        val f = fragmentManager.findFragmentByTag(key)
        if (f is DialogFragment)
        {
            f.dismiss()
            fragmentManager.executePendingTransactions()

            if (fragmentManager.backStackEntryCount == 0)
                closeAll()
        }
    }

    protected fun showSubFragment(@IdRes containerId: Int, view: View)
    {
        if (view is Fragment)
        {
            fragmentManager
                .beginTransaction()
                .replace(containerId, view, view.viewKey)
                .commit()

            fragmentManager.executePendingTransactions()
        }
    }
}