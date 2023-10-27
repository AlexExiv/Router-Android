package com.speakerboxlite.router.command

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
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.View
import com.speakerboxlite.router.controllers.AnimationController
import com.speakerboxlite.router.ext.isPopped
import com.speakerboxlite.router.ext.isRemovingRecursive
import java.io.Serializable

class CommandExecutorAndroid(val activity: FragmentActivity,
                             @IdRes val containerId: Int,
                             val fragmentManager: FragmentManager,
                             val activityFactory: HostActivityFactory): CommandExecutor
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
        activity.runOnUiThread { _execute(command) }
    }

    private fun _execute(command: Command)
    {
        when (command)
        {
            is Command.Close -> close()
            is Command.CloseTo -> closeTo(command.key)
            is Command.CloseAll -> activity.finish()
            is Command.StartModal -> startActivity(command.key, command.params)
            is Command.Dialog -> showDialog(command.view)
            is Command.CloseDialog -> closeDialog(command.key)
            is Command.Push -> pushFragment(command.path, command.view, command.animation, false)
            is Command.Replace -> replaceFragment(command.path, command.byView, command.animation)
            is Command.BottomSheet -> showBottomSheet(command.view)
            is Command.CloseBottomSheet -> closeBottomSheet(command.key)
            is Command.SubFragment -> showSubFragment(command.containerId, command.view)
            is Command.ChangeTab -> command.tabChangeCallback(command.tab)
        }
    }

    private fun close()
    {
        if (fragmentManager.backStackEntryCount > 1)
        {
            fragmentManager.executePendingTransactions()
            fragmentManager.popBackStack()
        }
        else
            activity.finish()
    }

    private fun closeTo(key: String)
    {
        if (fragmentManager.backStackEntryCount > 1)
        {
            fragmentManager.executePendingTransactions()
            fragmentManager.popBackStackImmediate(key, 0)
        }
    }

    private fun startActivity(key: String, params: Serializable?)
    {
        val intent = activityFactory.create(params)
        intent.putExtra(HOST_ACTIVITY_KEY, key)
        params?.also { intent.putExtra(HOST_ACTIVITY_INTENT_DATA_KEY, it) }
        activity.startActivity(intent)
    }

    private fun pushFragment(path: RoutePath, view: View, animation: AnimationController<RoutePath, View>?, replacing: Boolean)
    {
        if (view is Fragment)
        {
            fragmentManager.executePendingTransactions()
            val transaction = fragmentManager.beginTransaction()

            if (animation != null)
            {
                val current = fragmentManager.findFragmentById(containerId)

                transaction.setReorderingAllowed(true)
                animation.onConfigureAnimation(path, transaction, current, view, replacing)
            }

            transaction
                .replace(containerId, view, view.viewKey)
                .addToBackStack(view.viewKey)
                .commit()
        }
    }

    private fun replaceFragment(path: RoutePath, byView: View, animation: AnimationController<RoutePath, View>?)
    {
        if (byView is Fragment)
        {
            fragmentManager.executePendingTransactions()
            fragmentManager.popBackStack()
            pushFragment(path, byView, animation, true)
        }
    }

    private fun showBottomSheet(view: View)
    {
        if (view is BottomSheetDialogFragment)
        {
            view.show(fragmentManager, view.viewKey)
        }
    }

    private fun closeBottomSheet(key: String)
    {
        val f = fragmentManager.findFragmentByTag(key)
        if (f is BottomSheetDialogFragment)
        {
            f.dismiss()
        }
    }

    private fun showDialog(view: View)
    {
        if (view is DialogFragment)
        {
            view.show(fragmentManager, view.viewKey)
        }
    }

    private fun closeDialog(key: String)
    {
        val f = fragmentManager.findFragmentByTag(key)
        if (f is DialogFragment)
        {
            f.dismiss()
        }
    }

    private fun showSubFragment(@IdRes containerId: Int, view: View)
    {
        if (view is Fragment)
        {
            fragmentManager.executePendingTransactions()
            fragmentManager
                .beginTransaction()
                .replace(containerId, view, view.viewKey)
                .commit()
        }
    }
}