package com.speakerboxlite.router.fragmentcompose

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.compose.AndroidFragment
import com.speakerboxlite.router.compose.ViewCompose
import com.speakerboxlite.router.ext.toBundle
import com.speakerboxlite.router.fragment.ext._viewKey
import java.util.UUID

class FragmentHostView<F: Fragment>(
    val fragmentKey: String,
    val clazz: Class<F>,
    val bundleString: String?
): ViewCompose
{
    override var viewKey: String = fragmentKey

    @Composable
    override fun Root()
    {
        AndroidFragment(
            clazz = clazz,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            arguments = bundleString?.toBundle() ?: Bundle())
        {
            //it._viewKey = fragmentKey
        }
    }
}