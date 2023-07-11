package cafe.adriel.voyager.navigator.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import cafe.adriel.voyager.navigator.core.DisposableEffectIgnoringConfiguration
import cafe.adriel.voyager.navigator.core.ScreenLifecycleOwner
import cafe.adriel.voyager.navigator.core.StackEvent

private val disposableEvents: Set<StackEvent> =
    setOf(StackEvent.Pop, StackEvent.Replace)

@Composable
internal fun NavigatorDisposableEffect(
    navigator: Navigator
) {
    DisposableEffectIgnoringConfiguration(navigator) {
        onDispose {
            for (screen in navigator.items) {
                navigator.dispose(screen)
            }
            navigator.clearEvent()
        }
    }
}

@Composable
internal fun StepDisposableEffect(
    navigator: Navigator
) {
    val currentScreens = navigator.items

    DisposableEffect(currentScreens) {
        onDispose {
            val newScreenKeys = navigator.items.map { it.key }
            if (navigator.lastEvent in disposableEvents) {
                currentScreens.filter { it.key !in newScreenKeys }.forEach {
                    navigator.dispose(it)
                }
                navigator.clearEvent()
            }
        }
    }
}

@Composable
internal fun LifecycleDisposableEffect(
    lifecycleOwner: ScreenLifecycleOwner
) {
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.onStart()
        onDispose(lifecycleOwner::onStop)
    }
}
