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
import com.speakerboxlite.router.fragment.ext.isPopped
import com.speakerboxlite.router.fragment.ext.isRemovingRecursive
import java.io.Serializable

open class CommandExecutorAndroid(val activity: FragmentActivity,
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

    override fun onBind()
    {
        fragmentManager.addOnBackStackChangedListener(backstackCallback)
    }

    override fun onUnbind()
    {
        fragmentManager.removeOnBackStackChangedListener(backstackCallback)
    }

    override fun execute(command: Command)
    {
        try
        {
            activity.runOnUiThread { _execute(command) }
        }
        catch (e: IllegalStateException)
        {
            activity.window.decorView.post { _execute(command) }
        }
    }

    private fun _execute(command: Command)
    {
        checkFragmentManager()

        when (command)
        {
            is Command.Close -> close()
            is Command.CloseTo -> closeTo(command.key)
            is Command.CloseAll -> closeAll()
            is Command.StartModal -> startActivity(command.key, command.params)
            is Command.ChangeHost -> changeHost(command.key, command.path, command.animation as? AnimationControllerFragment<RoutePath, View>)
            is Command.Dialog -> showDialog(command.view)
            is Command.CloseDialog -> closeDialog(command.key)
            is Command.Push -> pushFragment(command.path, command.view, command.animation as? AnimationControllerFragment<RoutePath, View>, false)
            is Command.Replace -> replaceFragment(command.path, command.byView, command.animation as? AnimationControllerFragment<RoutePath, View>)
            is Command.BottomSheet -> showBottomSheet(command.view)
            is Command.CloseBottomSheet -> closeBottomSheet(command.key)
            is Command.SubFragment -> showSubFragment(command.containerId, command.view)
            is Command.ChangeTab -> command.tabChangeCallback(command.tab)
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

    protected open fun changeHost(key: String, path: RoutePath?, animation: AnimationControllerFragment<RoutePath, View>?)
    {
        error("You try to change a host but don't use appropriate Navigator")
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
        }
    }

    protected open fun closeBottomSheet(key: String)
    {
        val f = fragmentManager.findFragmentByTag(key)
        if (f is BottomSheetDialogFragment)
        {
            f.dismiss()

            if (fragmentManager.backStackEntryCount == 0)
                closeAll()
        }
    }

    protected open fun showDialog(view: View)
    {
        if (view is DialogFragment)
        {
            view.show(fragmentManager, view.viewKey)
        }
    }

    protected open fun closeDialog(key: String)
    {
        val f = fragmentManager.findFragmentByTag(key)
        if (f is DialogFragment)
        {
            f.dismiss()

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