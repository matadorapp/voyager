package cafe.adriel.voyager.navigator.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.HasDefaultViewModelProviderFactory
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
import java.util.concurrent.atomic.AtomicReference

class AndroidScreenLifecycleOwner private constructor() :
    ScreenLifecycleOwner,
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {

    override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)

    override val viewModelStore: ViewModelStore = ViewModelStore()

    private val atomicContext = AtomicReference<Context>()

    private val controller = SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry
        get() = controller.savedStateRegistry

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = SavedStateViewModelFactory(
            application = atomicContext.get()?.applicationContext?.getApplication(),
            owner = this
        )

    override val defaultViewModelCreationExtras: CreationExtras
        get() = MutableCreationExtras().apply {
            val application = atomicContext.get()?.applicationContext?.getApplication()
            if (application != null) {
                set(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY, application)
            }
            set(SAVED_STATE_REGISTRY_OWNER_KEY, this@AndroidScreenLifecycleOwner)
            set(VIEW_MODEL_STORE_OWNER_KEY, this@AndroidScreenLifecycleOwner)

            /* TODO if (getArguments() != null) {
                extras.set<Bundle>(DEFAULT_ARGS_KEY, getArguments())
            }*/
        }

    init {
        controller.performAttach()
        enableSavedStateHandles()
    }

    @Composable
    override fun ProvideBeforeScreenContent(
        provideSaveableState: @Composable (suffixKey: String, content: @Composable () -> Unit) -> Unit,
        content: @Composable () -> Unit
    ) {
        val hooks = getHooks()
        CompositionLocalProvider(*hooks.toTypedArray()) {
            content()
        }
    }

    override fun onDispose(screen: Screen) {
        val context = atomicContext.getAndSet(null) ?: return
        if (context is Activity && context.isChangingConfigurations) return
        viewModelStore.clear()
    }

    @Composable
    private fun getHooks(): List<ProvidedValue<*>> {
        atomicContext.compareAndSet(null, LocalContext.current)

        return remember(this) {
            listOf(
                LocalViewModelStoreOwner provides this,
                LocalSavedStateRegistryOwner provides this
            )
        }
    }

    private tailrec fun Context.getApplication(): Application? = when (this) {
        is Application -> this
        is ContextWrapper -> baseContext.getApplication()
        else -> null
    }

    companion object {
        fun get(screen: Screen): ScreenLifecycleOwner {
            return ScreenLifecycleStore.register(screen) { AndroidScreenLifecycleOwner() }
        }
    }
}
