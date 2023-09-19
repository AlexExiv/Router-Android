package com.speakerboxlite.router.command

import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.speakerboxlite.router.HOST_ACTIVITY_KEY
import com.speakerboxlite.router.HostActivityFactory
import com.speakerboxlite.router.View

class CommandExecutorAndroid(val activity: FragmentActivity,
                             @IdRes val containerId: Int,
                             val fragmentManager: FragmentManager,
                             val activityFactory: HostActivityFactory): CommandExecutor
{
    override fun execute(command: Command)
    {
        when (command)
        {
            is Command.Close -> close()
            is Command.CloseTo -> closeTo(command.key)
            is Command.StartModal -> startActivity(command.key)
            is Command.Dialog -> showDialog(command.view)
            is Command.CloseDialog -> closeDialog(command.key)
            is Command.Push -> pushFragment(command.view)
            is Command.BottomSheet -> showBottomSheet(command.view)
            is Command.CloseBottomSheet -> closeBottomSheet(command.key)
            is Command.SubFragment -> showSubFragment(command.containerId, command.view)
            is Command.ChangeTab -> command.tabChangeCallback(command.tab)
        }
    }

    private fun close()
    {
        if (fragmentManager.backStackEntryCount > 1)
            fragmentManager.popBackStack()
        else
            activity.finish()
    }

    private fun closeTo(key: String)
    {
        if (fragmentManager.backStackEntryCount > 1)
            fragmentManager.popBackStackImmediate(key, 0)
    }

    private fun startActivity(key: String)
    {
        val intent = activityFactory.create()
        intent.putExtra(HOST_ACTIVITY_KEY, key)
        activity.startActivity(intent)
    }

    private fun pushFragment(view: View)
    {
        if (view is Fragment)
        {
            fragmentManager
                .beginTransaction()
                .replace(containerId, view, view.viewKey)
                .addToBackStack(view.viewKey)
                .commit()
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
            fragmentManager
                .beginTransaction()
                .replace(containerId, view, view.viewKey)
                .commit()
        }
    }
}