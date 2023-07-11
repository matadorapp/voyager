package cafe.adriel.voyager.navigator.core

import androidx.compose.runtime.ProvidedValue

data class ScreenLifecycleHooks(
    val providers: List<ProvidedValue<*>> = emptyList()
) {

    internal companion object {
        val Empty = ScreenLifecycleHooks()
    }
}
