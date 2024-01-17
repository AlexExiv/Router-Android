package com.speakerboxlite.router.fragment.ext

import android.os.Bundle
import androidx.fragment.app.Fragment

val Fragment.isRemovingRecursive: Boolean get() = isRemoving || (parentFragment?.isRemovingRecursive ?: false)

var Fragment.isPopped: Boolean
    get() = arguments?.getBoolean("com.speakerboxlite.router.ROUTER_POPPED_KEY", false) ?: false
    set(value)
    {
        if (arguments == null)
            arguments = Bundle()
        requireArguments().putBoolean("com.speakerboxlite.router.ROUTER_POPPED_KEY", value)
    }

val Fragment.isPoppedRecursive: Boolean get() = isPopped || (parentFragment?.isPoppedRecursive ?: false)
