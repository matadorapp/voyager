package cafe.adriel.voyager.navigator.core

import androidx.compose.runtime.Composable

interface ScreenLifecycleOwner {

    @Composable
    fun getHooks(): ScreenLifecycleHooks = ScreenLifecycleHooks.Empty

    fun onDispose(screen: Screen) {}

    fun onStart() {}

    fun onStop() {}
}

internal object DefaultScreenLifecycleOwner : ScreenLifecycleOwner
