package com.speakerboxlite.router.compose

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class StackEntry(
    val view: ViewCompose,
    val viewModelProvider: RouterViewModelStoreProvider?,
    val animationController: AnimationControllerCompose?,
    isPopped: Boolean = false,
    val subKeys: MutableList<String> = mutableListOf(),
    private val savedState: Bundle? = null):
        ViewModelStoreOwner,
        HasDefaultViewModelProviderFactory,
        SavedStateRegistryOwner
{
    constructor(entry: StackEntrySaveable, viewModelProvider: RouterViewModelStoreProvider?):
            this(entry.view, viewModelProvider, entry.animationController, entry.isRemoving, entry.subKeys.toMutableList(), entry.savedState)

    val id: String get() = view.viewKey

    override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)

    override val viewModelStore: ViewModelStore get() = viewModelProvider?.getStore(id) ?: error("")

    override val defaultViewModelCreationExtras: CreationExtras
        get()
        {
            val extras = MutableCreationExtras()
            (context?.applicationContext as? Application)?.let { extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] = it }
            extras[SAVED_STATE_REGISTRY_OWNER_KEY] = this
            extras[VIEW_MODEL_STORE_OWNER_KEY] = this
            return extras
        }

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory get() = defaultFactory

    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private var context: Context? = null
    private var parentLifecycleOwner: LifecycleOwner? = null

    private val defaultFactory by lazy { SavedStateViewModelFactory((context?.applicationContext as? Application), this) }

    var isCreated: Boolean = false
        private set
    var isRemoving: Boolean = isPopped
        private set

    @Composable
    fun LocalOwnersProvider(saveableStateHolder: SaveableStateHolder, content: @Composable () -> Unit)
    {
        context = LocalContext.current
        parentLifecycleOwner = LocalLifecycleOwner.current

        LifecycleDisposableEffect()

        CompositionLocalProvider(
            LocalViewModelStoreOwner provides this,
            LocalLifecycleOwner provides this,
            LocalViewKey provides id)
        {
            saveableStateHolder.SaveableStateProvider(id, content)
        }
    }

    fun addSubView(key: String)
    {
        subKeys.add(key)
    }

    fun removeSubView(key: String)
    {
        subKeys.remove(key)
    }

    fun onCreate()
    {
        if (isCreated)
            return

        isCreated = true
        savedStateRegistryController.performAttach()
        enableSavedStateHandles()
        savedStateRegistryController.performRestore(savedState)

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    fun onStart()
    {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun onResume()
    {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun onSaveState(outBundle: Bundle)
    {
        savedStateRegistryController.performSave(outBundle)
    }

    fun onPause()
    {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    fun onStop()
    {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    fun onDispose()
    {
        if (isCreated)
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        context = null
        parentLifecycleOwner = null
        viewModelProvider?.clear(id)
    }

    internal fun makeRemoving()
    {
        isRemoving = true
    }

    @Composable
    private fun LifecycleDisposableEffect()
    {
        onCreate()

        DisposableEffect(key1 = this) {
            onStart()
            onResume()

            val observer = object : DefaultLifecycleObserver
            {
                override fun onPause(owner: LifecycleOwner)
                {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                }

                override fun onResume(owner: LifecycleOwner)
                {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                }

                override fun onStart(owner: LifecycleOwner)
                {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
                }

                override fun onStop(owner: LifecycleOwner)
                {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                }
            }

            val lifecycle = parentLifecycleOwner?.lifecycle
            lifecycle?.addObserver(observer)

            onDispose {
                lifecycle?.removeObserver(observer)

                onPause()
                onStop()
            }
        }
    }
}