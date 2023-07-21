package cafe.adriel.voyager.navigator.core

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import java.util.concurrent.atomic.AtomicReference

class AndroidScreenLifecycleOwner private constructor() :
    ScreenLifecycleOwner,
    ViewModelStoreOwner {

    private val store = ViewModelStore()

    private val atomicContext = AtomicReference<Context>()

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
        atomicContext.compareAndSet(
            null,
            LocalContext.current
        )

        return remember(this) {
            listOf(
                LocalViewModelStoreOwner provides this,
            )
        }
    }

    override val viewModelStore: ViewModelStore = store

    companion object {

        fun get(screen: Screen): ScreenLifecycleOwner {
            return ScreenLifecycleStore.register(screen) { AndroidScreenLifecycleOwner() }
        }
    }
}
